package com.example.batallanaval.controller;

import com.example.batallanaval.concurrency.MachineTurnService;
import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.ShotResult;
import com.example.batallanaval.service.GameManager;
import com.example.batallanaval.service.RandomFleetPlacer;
import com.example.batallanaval.strategy.RandomShootingStrategy;
import com.example.batallanaval.view.BoardView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller for the game screen. It connects the model ({@link GameManager}) with the
 * two {@link BoardView}s: it builds the boards, forwards the human's clicks as shots
 * (HU-2), redraws after each shot, runs the machine's response on a background thread
 * (criterion 7), and updates the status.
 * <p>
 * In this version both fleets are placed randomly so shooting can be tested end to end.
 * Manual human placement (HU-1) replaces the random human fleet in a later phase.
 *

 */
public class GameController {

    @FXML private Label statusLabel;
    @FXML private StackPane positionBoardContainer;
    @FXML private StackPane mainBoardContainer;
    @FXML private Button revealButton;

    private GameManager game;
    private BoardView positionBoardView;  // human's own board (ships visible)
    private BoardView mainBoardView;      // enemy board (ships hidden)
    private boolean enemyRevealed;

    private MachineTurnService machineTurnService;

    /**
     * Called automatically by JavaFX after the FXML is loaded. Sets up a new game.
     */
    @FXML
    public void initialize() {
        startNewGame("Player");
    }

    /**
     * Builds a fresh game with both fleets placed randomly and wires the boards and
     * the background machine-turn service.
     *
     * @param nickname the human player's nickname
     */
    public void startNewGame(String nickname) {
        RandomFleetPlacer placer = new RandomFleetPlacer();
        Board humanBoard = placer.createBoardWithRandomFleet();
        Board machineBoard = placer.createBoardWithRandomFleet();

        this.game = new GameManager(
                nickname, humanBoard, machineBoard, new RandomShootingStrategy());
        this.enemyRevealed = false;

        // Human board: ships visible. Enemy board: ships hidden, clickable to shoot.
        positionBoardView = new BoardView(true);
        mainBoardView = new BoardView(false);
        mainBoardView.setOnCellClick(this::handleHumanShot);

        positionBoardContainer.getChildren().setAll(positionBoardView);
        mainBoardContainer.getChildren().setAll(mainBoardView);

        // Background service for the machine's turn (criterion 7).
        machineTurnService = new MachineTurnService(game);
        // The callback runs on a BACKGROUND thread, so every UI update goes through
        // Platform.runLater to reach the JavaFX Application Thread safely.
        machineTurnService.setOnShot(result -> Platform.runLater(() -> {
            refreshBoards();
            showShotMessage("La maquina disparo", result);
            if (game.isGameOver()) {
                announceWinner();
            }
        }));

        refreshBoards();
        statusLabel.setText("Tu turno: dispara en el tablero principal.");
    }

    /**
     * Handles a click on the enemy board: the human shoots at that coordinate (HU-2).
     */
    private void handleHumanShot(Coordinate coordinate) {
        // Ignore clicks when it is not the human's turn or the game ended.
        if (game.isGameOver() || game.getCurrentTurn() != Player.HUMAN) {
            return;
        }

        try {
            ShotResult result = game.playerShootAt(coordinate);
            refreshBoards();
            showShotMessage("Disparaste", result);
        } catch (IllegalStateException alreadyShot) {
            // Cell already fired at: tell the user and let them pick another.
            statusLabel.setText("Ya disparaste ahi. Elige otra casilla.");
            return;
        }

        if (game.isGameOver()) {