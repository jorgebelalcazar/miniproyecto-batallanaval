package com.example.batallanaval.controller;

import com.example.batallanaval.factory.ShipFactory;
import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.InvalidPlacementException;
import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;
import com.example.batallanaval.service.RandomFleetPlacer;
import com.example.batallanaval.view.BoardView;
import com.example.batallanaval.view.DraggableShip;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.Queue;

/**
 * Controller for the manual ship placement screen (HU-1). The player drags each ship
 * from the tray onto the position board; ships can be horizontal or vertical and may
 * not overlap or fall off the board. When the whole fleet is placed, the game starts.
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

    private DraggableShip shipBeingDragged;   // the tray ship currently being dragged

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
     * the tray of ships, and wires the board as a drop target.
     */
    @FXML
    public void initialize() {
        board = new Board();
        boardView = new BoardView(true);
        boardView.render(board);
        boardContainer.getChildren().setAll(boardView);

        // The board reports drag-over (for preview) and drop (to place the ship).
        boardView.setOnCellDragDropped(this::handleDropAt);

        fillTray();
    }

    /**
     * Fills the tray with one draggable ship per ship in the standard fleet and makes
     * each one a drag source.
     */
    private void fillTray() {
        shipTray.getChildren().clear();
        Queue<ShipType> fleet = shipFactory.standardFleetTypes();
        while (!fleet.isEmpty()) {
            ShipType type = fleet.poll();
            DraggableShip ship = new DraggableShip(type);
            makeDraggable(ship);
            shipTray.getChildren().add(ship);
        }
    }

    /**
     * Turns a tray ship into a drag source: starting a drag records which ship is
     * moving so the drop handler knows what to place.
     */
    private void makeDraggable(DraggableShip ship) {
        ship.setOnDragDetected(event -> {
            shipBeingDragged = ship;
            Dragboard dragboard = ship.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // The content is required by JavaFX, but we track the ship in a field.
            content.putString(ship.getShipType().name());
            dragboard.setContent(content);
            event.consume();
        });
    }

    /**
     * Handles a ship dropped at the given starting coordinate. Builds the ship with the
     * current orientation and tries to place it; on success it is drawn and removed from
     * the tray, otherwise a message is shown (HU-1 validation).
     */
    private void handleDropAt(Coordinate start) {
        if (shipBeingDragged == null) {
            return;
        }
        ShipType type = shipBeingDragged.getShipType();
        Ship ship = shipFactory.createShip(type, start, currentOrientation);

        try {
            board.placeShip(ship);              // validates overlap and bounds (HU-1)
            boardView.render(board);            // draw the placed ship
            shipTray.getChildren().remove(shipBeingDragged);  // remove from tray
            statusLabel.setText("Barco colocado: " + type + ".");
            checkFleetComplete();
        } catch (InvalidPlacementException e) {
            statusLabel.setText("Ahi no cabe: " + e.getMessage() + " Intenta en otro lugar.");
        } finally {
            shipBeingDragged = null;
        }
    }

    /**
     * Enables the start button once every ship has been placed (tray empty).
     */
    private void checkFleetComplete() {
        if (shipTray.getChildren().isEmpty()) {
            startButton.setDisable(false);
            statusLabel.setText("Flota completa. Pulsa 'Empezar partida'.");
        }
    }

    /**
     * Toggles the orientation used when a ship is dropped, reflected in the tray and
     * the button label.
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
     * Places the whole fleet randomly as a shortcut, clearing the tray (safety net).
     */
    @FXML
    private void onRandom() {
        board = new RandomFleetPlacer().createBoardWithRandomFleet();
        boardView.render(board);
        boardView.setOnCellDragDropped(this::handleDropAt); // keep target on new board
        shipTray.getChildren().clear();
        startButton.setDisable(false);
        statusLabel.setText("Flota colocada aleatoriamente. Pulsa 'Empezar partida'.");
    }

    /**
     * Placeholder: wired in step 3 (start the game with the placed fleet).
     */
    @FXML
    private void onStart() {
        statusLabel.setText("Empezar partida se conecta en el paso 3.");
    }
}