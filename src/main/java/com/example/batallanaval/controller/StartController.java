package com.example.batallanaval.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the start view, where the player enters a nickname and
 * chooses to start a new game or continue a previously saved one.
 * <p>
 * This controller belongs to the controller layer of the MVC architecture.
 * In this phase it validates the nickname and reports the chosen action;
 * navigation to the game view and loading saved games are wired in later
 * phases.
 * </p>
 *
 */
public class StartController implements Initializable {

    /** Text field where the player types a nickname. */
    @FXML
    private TextField nicknameField;

    /** Button that starts a brand-new game. */
    @FXML
    private Button newGameButton;

    /** Button that continues a previously saved game. */
    @FXML
    private Button continueButton;

    /** Label used to display validation or status messages. */
    @FXML
    private Label statusLabel;

    /**
     * Initialization callback invoked after the FXML view is loaded. Wires
     * the button actions.
     *
     * @param location  the FXML location (unused)
     * @param resources the resource bundle (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newGameButton.setOnAction(event -> handleNewGame());
        continueButton.setOnAction(event -> handleContinue());
    }

    /**
     * Handles the "new game" action: validates the nickname and, in later
     * phases, requests the game view. For now it reports the chosen action.
     */
    private void handleNewGame() {
        String nickname = nicknameField.getText();
        if (nickname == null || nickname.isBlank()) {
            statusLabel.setText("Por favor ingresa un nickname para continuar.");
            return;
        }
        statusLabel.setText("Nuevo juego para: " + nickname.trim()
                + ". (La pantalla de juego se implementara en la siguiente fase.)");
    }

    /**
     * Handles the "continue" action: in later phases it will load the most
     * recent saved game. For now it reports the action as a placeholder.
     */
    private void handleContinue() {
        statusLabel.setText("Continuar partida guardada. "
                + "(La carga de guardado se implementara en la fase de persistencia.)");
    }
}