package com.example.batallanaval.view;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Renders a full 10x10 board as a grid of {@link CellView} objects, with column
 * labels (A-J) and row labels (1-10) as shown in the statement.
 * <p>
 * This is a pure View component. It knows how to draw a {@link Board} and how to
 * report clicks (as {@link Coordinate}s) to a listener, but it does not know the game
 * rules nor what should happen on a click. That keeps the View decoupled from the
 * Controller (criterion 4).
 *
 */
public class BoardView extends GridPane {

    private static final double CELL_SIZE = 34;

    private final Map<Coordinate, CellView> cellViews;
    private boolean revealShips;
    private Consumer<Coordinate> onCellClick;

    /**
     * Builds an empty board view (all water) with its labels.
     *
     * @param revealShips whether ship cells should be drawn visible (true for the
     *                    human's own board, false for the enemy board)
     */
    public BoardView(boolean revealShips) {
        this.cellViews = new HashMap<>();
        this.revealShips = revealShips;
        setHgap(1);
        setVgap(1);
        buildLabels();
        buildCells();
    }

    private void buildLabels() {
        // Column labels A..J on the top row.
        for (int column = 0; column < Board.SIZE; column++) {
            Label label = new Label(String.valueOf((char) ('A' + column)));
            label.setPrefSize(CELL_SIZE, CELL_SIZE);
            label.setTextFill(Color.web("#1b4f72"));
            label.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            add(label, column + 1, 0);
        }
        // Row labels 1..10 on the left column.
        for (int row = 0; row < Board.SIZE; row++) {
            Label label = new Label(String.valueOf(row + 1));
            label.setPrefSize(CELL_SIZE, CELL_SIZE);
            label.setTextFill(Color.web("#1b4f72"));
            label.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            add(label, 0, row + 1);
        }
    }

    private void buildCells() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                CellView cellView = new CellView(CELL_SIZE);

                // Report the click to the listener, if any. The view itself does
                // nothing else: the controller decides what a click means.
                cellView.setOnMouseClicked(event -> {
                    if (onCellClick != null) {
                        onCellClick.accept(coordinate);
                    }
                });

                cellViews.put(coordinate, cellView);
                // Model (row, column) -> grid (column + 1, row + 1) to leave room
                // for the labels.
                add(cellView, column + 1, row + 1);
            }
        }
    }

    /**
     * Redraws every cell to match the given board's current state.
     *
     * @param board the model board to reflect
     */
    public void render(Board board) {
        for (Map.Entry<Coordinate, CellView> entry : cellViews.entrySet()) {
            Coordinate coordinate = entry.getKey();
            entry.getValue().render(board.getCellState(coordinate), revealShips);
        }
    }

    /**
     * Sets the listener invoked when the user clicks a cell, receiving that cell's
     * coordinate. Used by the controller to handle placement (HU-1) and shots (HU-2).
     *
     * @param onCellClick the click listener
     */
    public void setOnCellClick(Consumer<Coordinate> onCellClick) {
        this.onCellClick = onCellClick;
    }

    /**
     * Changes whether ships are drawn visible and redraws. Used for the "reveal enemy
     * board" verification option (HU-3).
     *
     * @param revealShips the new visibility for ship cells
     * @param board       the board to redraw with the new setting
     */
    public void setRevealShips(boolean revealShips, Board board) {
        this.revealShips = revealShips;
        render(board);
    }
}