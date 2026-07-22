package com.example.batallanaval.controller;

import com.example.batallanaval.persistence.PersistenceService;
import com.example.batallanaval.service.GameManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controller for the start screen (nickname + New Game / Continue).
 * <p>
 * "New Game" opens the game screen and starts a fresh game with the entered nickname.
 * "Continue" loads the most recent saved game and resumes it exactly (HU-5). If there
 * is no saved game, the Continue button is disabled.
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
     * Starts a brand new game, requiring a non-empty nickname (HU-5 base).
     */
    private void onNewGame() {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            statusLabel.setText("Por favor ingresa un nickname.");
            return;
        }
        openGameScreen(controller -> controller.startNewGame(nickname));
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
            openGameScreen(controller -> controller.resumeGame(saved.get()));
        } catch (IOException | ClassNotFoundException e) {
            statusLabel.setText("No se pudo cargar la partida guardada.");
        }
    }

    /**
     * Loads the game view, obtains its controller, lets the caller initialize it
     * (new game or resume), and swaps the current scene to the game screen.
     *
     * @param init action that starts or resumes the game on the game controller
     */
    private void openGameScreen(Consumer<GameController> init) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/batallanaval/view/game-view.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            init.accept(controller);   // startNewGame(nickname) or resumeGame(savedGame)

            Stage stage = (Stage) newGameButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            statusLabel.setText("No se pudo abrir la pantalla de juego.");
        }
    }
}