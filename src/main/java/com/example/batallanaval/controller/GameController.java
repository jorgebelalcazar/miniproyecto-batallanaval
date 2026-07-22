package com.example.batallanaval.controller;

import com.example.batallanaval.concurrency.GameTimer;
import com.example.batallanaval.concurrency.MachineTurnService;
import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.ShotResult;
import com.example.batallanaval.persistence.PersistenceService;
import com.example.batallanaval.service.GameManager;
import com.example.batallanaval.service.RandomFleetPlacer;
import com.example.batallanaval.strategy.RandomShootingStrategy;
import com.example.batallanaval.view.BoardView;
import com.example.batallanaval.view.ViewManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for the game screen. It connects the model ({@link GameManager}) with the
 * two {@link BoardView}s: it builds the boards, forwards the human's clicks as shots
 * (HU-2), redraws after each shot, runs the machine's response on a background thread
 * and a game timer on a second thread (criterion 7), auto-saves after every play and
 * can resume a saved game (HU-5), offers a "back to menu" control (usability,
 * criterion 1), and updates the status.
 * <p>
 * The game is started by the start/placement screens, which call one of the
 * {@code startNewGame} overloads or {@link #resumeGame(GameManager)}.
 *
 */
public class GameController {

    @FXML private Label statusLabel;
    @FXML private Label timerLabel;
    @FXML private StackPane positionBoardContainer;
    @FXML private StackPane mainBoardContainer;
    @FXML private Button revealButton;
    @FXML private Button backButton;

    private final PersistenceService persistence = new PersistenceService();

    private GameManager game;
    private BoardView positionBoardView;  // human's own board (ships visible)
    private BoardView mainBoardView;      // enemy board (ships hidden)
    private boolean enemyRevealed;

    private MachineTurnService machineTurnService;  // first thread
    private GameTimer gameTimer;                    // second thread

    /**
     * Called automatically by JavaFX after the FXML is loaded. The actual game is
     * started by the start/placement screens.
     */
    @FXML
    public void initialize() {
        // Intentionally empty: the start/placement screens decide how to start.
    }

    /**
     * Starts a new game using a human board already populated by the player (HU-1).
     * The machine board is generated randomly.
     *
     * @param nickname   the player's nickname
     * @param humanBoard the board the player filled in during placement
     */
    public void startNewGame(String nickname, Board humanBoard) {
        Board machineBoard = new RandomFleetPlacer().createBoardWithRandomFleet();
        this.game = new GameManager(
                nickname, humanBoard, machineBoard, new RandomShootingStrategy());
        wireGame();
    }

    /**
     * Starts a new game with both fleets placed randomly (kept as a fallback).
     *
     * @param nickname the player's nickname
     */
    public void startNewGame(String nickname) {
        Board humanBoard = new RandomFleetPlacer().createBoardWithRandomFleet();
        startNewGame(nickname, humanBoard);
    }

    /**
     * Resumes a previously saved game, restoring its exact state (HU-5).
     *
     * @param savedGame the game loaded from disk
     */
    public void resumeGame(GameManager savedGame) {
        this.game = savedGame;
        wireGame();
    }

    /**
     * Wires the current {@code game} to the views, the threads and the click handlers.
     * Shared by a new game and a resumed one.
     */
    private void wireGame() {
        this.enemyRevealed = false;

        positionBoardView = new BoardView(true);
        mainBoardView = new BoardView(false);
        mainBoardView.setOnCellClick(this::handleHumanShot);

        positionBoardContainer.getChildren().setAll(positionBoardView);
        mainBoardContainer.getChildren().setAll(mainBoardView);

        // First thread: background service for the machine's turn (criterion 7).
        machineTurnService = new MachineTurnService(game);
        machineTurnService.setOnShot(result -> Platform.runLater(() -> {
            refreshBoards();
            showShotMessage("La maquina disparo", result);
            autoSave();                 // HU-5: save after the machine's play
            if (game.isGameOver()) {
                announceWinner();
            }
        }));

        // Second thread: the game timer, running in parallel (criterion 7).
        startTimer();

        refreshBoards();

        // If we resumed mid-machine-turn, let the machine continue playing.
        if (!game.isGameOver() && game.getCurrentTurn() == Player.MACHINE) {
            playMachineTurn();
            statusLabel.setText("Turno de la maquina...");
        } else {
            statusLabel.setText("Tu turno: dispara en el tablero principal.");
        }
    }

    /**
     * Starts (or restarts) the game timer on its own daemon thread, binding the timer
     * label to its message property so UI updates are delivered safely.
     */
    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        gameTimer = new GameTimer();
        timerLabel.textProperty().unbind();
        timerLabel.textProperty().bind(gameTimer.messageProperty());
        Thread timerThread = new Thread(gameTimer);
        timerThread.setDaemon(true);
        timerThread.start();
    }

    /**
     * Handles a click on the enemy board: the human shoots at that coordinate (HU-2).
     */
    private void handleHumanShot(Coordinate coordinate) {
        if (game.isGameOver() || game.getCurrentTurn() != Player.HUMAN) {
            return;
        }

        try {
            ShotResult result = game.playerShootAt(coordinate);
            refreshBoards();
            showShotMessage("Disparaste", result);
            autoSave();                 // HU-5: save after the human's play
        } catch (IllegalStateException alreadyShot) {
            statusLabel.setText("Ya disparaste ahi. Elige otra casilla.");
            return;
        }

        if (game.isGameOver()) {
            announceWinner();
            return;
        }

        if (game.getCurrentTurn() == Player.MACHINE) {
            playMachineTurn();
        }
    }

    /**
     * Starts the machine's turn on a background thread (criterion 7).
     */
    private void playMachineTurn() {
        machineTurnService.restart();
    }

    /**
     * HU-3: toggles the visibility of the enemy fleet for verification.
     */
    @FXML
    private void onToggleReveal() {
        enemyRevealed = !enemyRevealed;
        mainBoardView.setRevealShips(enemyRevealed, game.getMachineBoard());
        revealButton.setText(enemyRevealed
                ? "Ocultar flota enemiga"
                : "Mostrar flota enemiga (verificacion)");
    }

    /**
     * Returns to the start menu (user control and freedom heuristic, criterion 1). The
     * game timer is stopped and the auto-saved game remains on disk, so the player can
     * resume later with "Continuar".
     */
    @FXML
    private void onBackToMenu() {
        if (gameTimer != null) {
            gameTimer.cancel();        // stop the background timer thread
        }
        ViewManager.getInstance().showStartView();
    }

    /**
     * Saves the game after every play (HU-5). A disk failure only shows a message.
     */
    private void autoSave() {
        try {
            persistence.saveGame(game);
        } catch (IOException e) {
            statusLabel.setText("Aviso: no se pudo guardar la partida.");
        }
    }

    private void refreshBoards() {
        positionBoardView.render(game.getHumanBoard());
        mainBoardView.render(game.getMachineBoard());
    }

    private void showShotMessage(String who, ShotResult result) {
        String outcome = switch (result) {
            case WATER -> "agua.";
            case HIT -> "tocado!";
            case SUNK -> "hundido!";
        };
        statusLabel.setText(who + ": " + outcome);
    }

    private void announceWinner() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        String winner = game.getWinner() == Player.HUMAN ? "Ganaste!" : "Gano la maquina.";
        statusLabel.setText("Fin del juego. " + winner);
    }
}