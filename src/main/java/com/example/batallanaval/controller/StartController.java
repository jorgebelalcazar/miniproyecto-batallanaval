package com.example.batallanaval.controller;

import com.example.batallanaval.persistence.PersistenceService;
import com.example.batallanaval.service.GameManager;
import com.example.batallanaval.view.ViewManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the start screen (nickname + New Game / Continue).
 * <p>
 * "New Game" opens the manual placement screen so the player positions the fleet
 * (HU-1). "Continue" loads the most recent saved game and resumes it (HU-5). If there
 * is no saved game, the Continue button is disabled. All navigation goes through the
 * {@link ViewManager}.
 *
 */
public class StartController {

    @FXML private TextField nicknameField;
    @FXML private Button newGameButton;
    @FXML private Button continueButton;
    @FXML private Label statusLabel;

    private final PersistenceService persistence = new PersistenceService();

    /**
     * Wires the buttons and disables "Continue" when there is no saved game.
     */
    @FXML
    public void initialize() {
        newGameButton.setOnAction(event -> onNewGame());
        continueButton.setOnAction(event -> onContinue());
        continueButton.setDisable(!persistence.hasSavedGame());
    }

    /**
     * Opens the manual placement screen so the player can position the fleet (HU-1).
     */
    private void onNewGame() {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            statusLabel.setText("Por favor ingresa un nickname.");
            return;
        }
        ViewManager.getInstance().showPlacementView(
                (PlacementController controller) -> controller.setNickname(nickname));
    }

    /**
     * Loads the most recent saved game and resumes it (HU-5).
     */
    private void onContinue() {
        try {
            Optional<GameManager> saved = persistence.loadGame();
            if (saved.isEmpty()) {
                statusLabel.setText("No hay ninguna partida guardada.");
                return;
            }
            ViewManager.getInstance().showGameView(
                    (GameController controller) -> controller.resumeGame(saved.get()));
        } catch (IOException | ClassNotFoundException e) {
            statusLabel.setText("No se pudo cargar la partida guardada.");
        }
    }
}