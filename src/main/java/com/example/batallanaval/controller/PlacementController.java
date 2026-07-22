package com.example.batallanaval.controller;

import com.example.batallanaval.factory.ShipFactory;
import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.ShipType;
import com.example.batallanaval.view.BoardView;
import com.example.batallanaval.view.DraggableShip;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.Queue;

/**
 * Controller for the manual ship placement screen (HU-1). The player drags each ship
 * from the tray onto the position board; ships can be horizontal or vertical and may
 * not overlap or fall off the board. When the whole fleet is placed, the game starts.
 * <p>
 * This is the step 1 skeleton: it builds the empty board and fills the tray. The drag
 * behaviour and the start action are wired in later steps.
 *
 */
public class PlacementController {

    @FXML private Label statusLabel;
    @FXML private StackPane boardContainer;
    @FXML private FlowPane shipTray;
    @FXML private Button rotateButton;
    @FXML private Button randomButton;
    @FXML private Button startButton;

    private final ShipFactory shipFactory = new ShipFactory();

    private Board board;
    private BoardView boardView;
    private Orientation currentOrientation = Orientation.HORIZONTAL;
    private String nickname = "Player";

    /**
     * Sets the nickname carried over from the start screen.
     *
     * @param nickname the player's nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Called automatically by JavaFX after the FXML loads. Builds the empty board and
     * the tray of ships to place.
     */
    @FXML
    public void initialize() {
        board = new Board();
        boardView = new BoardView(true);   // player's own board: ships will be visible
        boardView.render(board);
        boardContainer.getChildren().setAll(boardView);

        fillTray();
    }

    /**
     * Fills the tray with one draggable ship per ship in the standard fleet
     * (1 carrier, 2 submarines, 3 destroyers, 4 frigates).
     */
    private void fillTray() {
        shipTray.getChildren().clear();
        Queue<ShipType> fleet = shipFactory.standardFleetTypes();
        while (!fleet.isEmpty()) {
            ShipType type = fleet.poll();
            shipTray.getChildren().add(new DraggableShip(type));
        }
    }

    /**
     * Toggles the orientation used when a ship is dropped, and reflects it in the
     * tray ships and the button label.
     */
    @FXML
    private void onRotate() {
        currentOrientation = (currentOrientation == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL : Orientation.HORIZONTAL;

        for (var node : shipTray.getChildren()) {
            if (node instanceof DraggableShip ship) {
                ship.toggleOrientation();
            }
        }
        rotateButton.setText(currentOrientation == Orientation.HORIZONTAL
                ? "Rotar (Horizontal)" : "Rotar (Vertical)");
    }

    /**
     * Placeholder: wired in step 2 (random auto-placement helper).
     */
    @FXML
    private void onRandom() {
        statusLabel.setText("La colocacion aleatoria se conecta en el paso 2.");
    }

    /**
     * Placeholder: wired in step 3 (start the game with the placed fleet).
     */
    @FXML
    private void onStart() {
        statusLabel.setText("Empezar partida se conecta en el paso 3.");
    }
}