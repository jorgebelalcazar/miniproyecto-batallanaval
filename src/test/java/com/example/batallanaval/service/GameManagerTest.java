package com.example.batallanaval.service;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.InvalidPlacementException;
import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;
import com.example.batallanaval.model.ShotResult;
import com.example.batallanaval.strategy.ShootingStrategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link GameManager}.
 * <p>
 * Uses a stub {@link ShootingStrategy} with predefined targets so the machine's
 * behaviour is deterministic, allowing the turn rules of HU-2 to be verified exactly.
 *
 */
class GameManagerTest {

    /**
     * A deterministic strategy that returns predefined targets in order.
     */
    private static class StubStrategy implements ShootingStrategy {
        private final Queue<Coordinate> targets;

        StubStrategy(Coordinate... coordinates) {
            this.targets = new ArrayDeque<>();
            for (Coordinate c : coordinates) {
                targets.add(c);
            }
        }

        @Override
        public Coordinate chooseTarget() {
            return targets.poll();
        }

        @Override
        public void registerResult(Coordinate coordinate, ShotResult result) {
            // No-op: the stub does not need to react.
        }
    }

    /**
     * Builds a game where the machine board has a single frigate at (0,0) and the
     * human board has a single frigate at (9,9).
     */
    private GameManager buildSimpleGame(ShootingStrategy strategy)
            throws InvalidPlacementException {
        Board humanBoard = new Board();
        humanBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(9, 9))));

        Board machineBoard = new Board();
        machineBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 0))));

        return new GameManager("Tester", humanBoard, machineBoard, strategy);
    }

    @Test
    @DisplayName("A water shot by the human passes the turn to the machine")
    void waterPassesTurnToMachine() throws InvalidPlacementException {
        GameManager game = buildSimpleGame(new StubStrategy());

        // (5,5) is empty on the machine board -> water.
        ShotResult result = game.playerShootAt(new Coordinate(5, 5));

        assertEquals(ShotResult.WATER, result);
        assertEquals(Player.MACHINE, game.getCurrentTurn());
    }

    @Test
    @DisplayName("A sinking shot by the human keeps the turn (and ends the game here)")
    void hitKeepsTheTurn() throws InvalidPlacementException {
        // Machine board has two frigates so sinking one does not end the game.
        Board humanBoard = new Board();
        humanBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(9, 9))));

        Board machineBoard = new Board();
        machineBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 0))));
        machineBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 1))));

        GameManager game =
                new GameManager("Tester", humanBoard, machineBoard, new StubStrategy());

        ShotResult result = game.playerShootAt(new Coordinate(0, 0)); // sinks one frigate

        assertEquals(ShotResult.SUNK, result);
        assertEquals(Player.HUMAN, game.getCurrentTurn()); // still the human's turn
        assertTrue(!game.isGameOver());
    }

    @Test
    @DisplayName("The machine cannot shoot while it is the human's turn")
    void machineCannotShootOnHumansTurn() throws InvalidPlacementException {
        GameManager game = buildSimpleGame(new StubStrategy(new Coordinate(9, 9)));

        // It is the human's turn at the start, so machineShoot must fail.
        assertThrows(IllegalStateException.class, game::machineShoot);
    }

    @Test
    @DisplayName("Sinking the whole enemy fleet ends the game with the human as winner")
    void humanWinsBySinkingWholeFleet() throws InvalidPlacementException {
        GameManager game = buildSimpleGame(new StubStrategy());

        game.playerShootAt(new Coordinate(0, 0)); // sinks the machine's only frigate

        assertTrue(game.isGameOver());
        assertEquals(Player.HUMAN, game.getWinner());
    }

    @Test
    @DisplayName("Shooting after the game is over throws IllegalStateException")
    void cannotShootAfterGameOver() throws InvalidPlacementException {
        GameManager game = buildSimpleGame(new StubStrategy());
        game.playerShootAt(new Coordinate(0, 0)); // human wins, game over

        assertThrows(IllegalStateException.class,
                () -> game.playerShootAt(new Coordinate(1, 1)));
    }

    @Test
    @DisplayName("The machine shoots at the human board and can win the game")
    void machineCanWin() throws InvalidPlacementException {
        // Machine board has two frigates; human board has one frigate at (9,9).
        Board humanBoard = new Board();
        humanBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(9, 9))));

        Board machineBoard = new Board();
        machineBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 0))));
        machineBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 1))));

        // Human misses (water at 5,5) so the turn passes to the machine.
        // The machine is set up to shoot (9,9), sinking the human's only frigate.
        GameManager game = new GameManager(
                "Tester", humanBoard, machineBoard,
                new StubStrategy(new Coordinate(9, 9)));

        game.playerShootAt(new Coordinate(5, 5));      // water -> machine's turn
        assertEquals(Player.MACHINE, game.getCurrentTurn());

        ShotResult result = game.machineShoot();       // machine sinks human frigate

        assertEquals(ShotResult.SUNK, result);
        assertTrue(game.isGameOver());
        assertEquals(Player.MACHINE, game.getWinner());
    }
}