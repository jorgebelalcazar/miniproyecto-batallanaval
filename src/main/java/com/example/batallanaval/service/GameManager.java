package com.example.batallanaval.service;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.ShotResult;
import com.example.batallanaval.strategy.ShootingStrategy;

import java.io.Serializable;

/**
 * Orchestrates a full game of Batalla Naval between the human and the machine.
 * <p>
 * It owns the two boards (the human shoots at the machine's board and vice versa),
 * enforces the turn rules of HU-2 (water passes the turn; hit or sunk repeats it),
 * delegates the machine's target choice to a {@link ShootingStrategy}, and detects
 * the win condition.
 * <p>
 * It does not know anything about JavaFX or how the game is drawn (that is the View's
 * and Controller's job) nor about threads. Implements {@link Serializable} so the full
 * game state can be saved and resumed (HU-5).
 *
 */
public class GameManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nickname;
    private final Board humanBoard;
    private final Board machineBoard;
    private final ShootingStrategy machineStrategy;

    private Player currentTurn;
    private boolean gameOver;
    private Player winner;

    /**
     * Creates a game with the two prepared boards and the machine's shooting strategy.
     * The human always shoots first.
     *
     * @param nickname        the human player's nickname (for persistence, HU-5)
     * @param humanBoard      the human's board, already populated with the human fleet
     * @param machineBoard    the machine's board, already populated with its fleet
     * @param machineStrategy the strategy the machine uses to choose targets
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public GameManager(String nickname, Board humanBoard, Board machineBoard,
                       ShootingStrategy machineStrategy) {
        if (nickname == null || humanBoard == null
                || machineBoard == null || machineStrategy == null) {
            throw new IllegalArgumentException("No argument can be null");
        }
        this.nickname = nickname;
        this.humanBoard = humanBoard;
        this.machineBoard = machineBoard;
        this.machineStrategy = machineStrategy;
        this.currentTurn = Player.HUMAN;
        this.gameOver = false;
        this.winner = null;
    }

    /**
     * Resolves a shot fired by the human at the machine's board (HU-2).
     *
     * @param coordinate the target on the machine's board
     * @return the result of the shot
     * @throws IllegalStateException if the game is over or it is not the human's turn
     */
    public ShotResult playerShootAt(Coordinate coordinate) {
        requireActiveTurn(Player.HUMAN);

        ShotResult result = machineBoard.receiveShot(coordinate);

        if (machineBoard.allShipsSunk()) {
            endGame(Player.HUMAN);
        } else if (result == ShotResult.WATER) {
            currentTurn = Player.MACHINE;
        }
        // On HIT or SUNK the turn stays with the human.
        return result;
    }

    /**
     * Performs one shot by the machine at the human's board, choosing the target
     * through its {@link ShootingStrategy} (HU-4).
     *
     * @return the result of the machine's shot
     * @throws IllegalStateException if the game is over or it is not the machine's turn
     */
    public ShotResult machineShoot() {
        requireActiveTurn(Player.MACHINE);

        Coordinate target = machineStrategy.chooseTarget();
        ShotResult result = humanBoard.receiveShot(target);
        machineStrategy.registerResult(target, result);

        if (humanBoard.allShipsSunk()) {
            endGame(Player.MACHINE);
        } else if (result == ShotResult.WATER) {
            currentTurn = Player.HUMAN;
        }
        return result;
    }

    /**
     * @return the player whose turn it currently is
     */
    public Player getCurrentTurn() {
        return currentTurn;
    }

    /**
     * @return {@code true} if the game has finished
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @return the winner, or {@code null} if the game is still ongoing
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * @return the number of machine ships the human has already sunk (for the flat
     *         file save, HU-5)
     */
    public int getEnemyShipsSunkByHuman() {
        return machineBoard.getSunkShipCount();
    }

    public String getNickname() {
        return nickname;
    }

    public Board getHumanBoard() {
        return humanBoard;
    }

    public Board getMachineBoard() {
        return machineBoard;
    }

    private void requireActiveTurn(Player expected) {
        if (gameOver) {
            throw new IllegalStateException("The game is already over");
        }
        if (currentTurn != expected) {
            throw new IllegalStateException("It is not " + expected + "'s turn");
        }
    }

    private void endGame(Player theWinner) {
        this.gameOver = true;
        this.winner = theWinner;
    }
}