package com.example.batallanaval.persistence;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.InvalidPlacementException;
import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;
import com.example.batallanaval.service.GameManager;
import com.example.batallanaval.strategy.RandomShootingStrategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PersistenceService}.
 * <p>
 * Verifies the core HU-5 promise: after saving and loading, the game returns to the
 * exact same state. Cleans up the save files before and after each test so it does
 * not interfere with real saved games.
 *
 * @author Jorge Belalcazar
 * @version 1.0
 */
class PersistenceServiceTest {

    private final PersistenceService persistence = new PersistenceService();

    private static final Path GAME_FILE = Path.of("saves", "game.dat");
    private static final Path PLAYER_FILE = Path.of("saves", "player.txt");

    @BeforeEach
    @AfterEach
    void cleanSaveFiles() throws IOException {
        Files.deleteIfExists(GAME_FILE);
        Files.deleteIfExists(PLAYER_FILE);
    }

    /**
     * Builds a small game and fires a couple of shots to create a non-trivial state.
     */
    private GameManager buildGameWithSomeShots() throws InvalidPlacementException {
        Board humanBoard = new Board();
        humanBoard.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(9, 9))));

        Board machineBoard = new Board();
        machineBoard.placeShip(new Ship(ShipType.DESTROYER, Set.of(
                new Coordinate(0, 0), new Coordinate(0, 1))));

        GameManager game = new GameManager(
                "Jorge", humanBoard, machineBoard, new RandomShootingStrategy());

        game.playerShootAt(new Coordinate(0, 0)); // HIT on the machine's destroyer
        return game;
    }

    @Test
    @DisplayName("A saved game loads back with the exact same state")
    void savedGameRestoresExactState() throws Exception {
        GameManager original = buildGameWithSomeShots();
        persistence.saveGame(original);

        GameManager loaded = persistence.loadGame().orElseThrow();

        // Same turn (human hit, so it is still the human's turn).
        assertEquals(original.getCurrentTurn(), loaded.getCurrentTurn());
        assertEquals(Player.HUMAN, loaded.getCurrentTurn());

        // The cell that was hit keeps its HIT state on the loaded machine board.
        assertEquals(
                original.getMachineBoard().getCellState(new Coordinate(0, 0)),
                loaded.getMachineBoard().getCellState(new Coordinate(0, 0)));

        // Same nickname and same game-over status.
        assertEquals(original.getNickname(), loaded.getNickname());
        assertEquals(original.isGameOver(), loaded.isGameOver());
    }

    @Test
    @DisplayName("The flat player file stores nickname and enemy ships sunk")
    void flatFileStoresPlayerData() throws Exception {
        GameManager game = buildGameWithSomeShots();
        persistence.saveGame(game);

        PersistenceService.PlayerData data = persistence.loadPlayerData().orElseThrow();

        assertEquals("Jorge", data.nickname());
        assertEquals(game.getEnemyShipsSunkByHuman(), data.enemyShipsSunk());
    }

    @Test
    @DisplayName("Loading with no previous save returns an empty Optional")
    void loadWithoutSaveReturnsEmpty() throws Exception {
        Optional<GameManager> loaded = persistence.loadGame();

        assertFalse(loaded.isPresent());
    }

    @Test
    @DisplayName("hasSavedGame reflects whether a save exists")
    void hasSavedGameReflectsState() throws Exception {
        assertFalse(persistence.hasSavedGame());

        persistence.saveGame(buildGameWithSomeShots());

        assertTrue(persistence.hasSavedGame());
    }
}