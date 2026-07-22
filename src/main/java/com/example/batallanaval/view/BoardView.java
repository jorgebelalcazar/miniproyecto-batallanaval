package com.example.batallanaval.view;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;

import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Renders a full 10x10 board as a grid of {@link CellView} objects, with column
 * labels (A-J) and row labels (1-10). It reports clicks and drag events (as
 * {@link Coordinate}s) to listeners, but it does not know the game rules.
 *
 */
public class BoardView extends GridPane {

    private static final double CELL_SIZE = 34;

    private final Map<Coordinate, CellView> cellViews;
    private boolean revealShips;
    private Consumer<Coordinate> onCellClick;
    private Consumer<Coordinate> onCellDragOver;      // NEW: drag hovering over a cell
    private Consumer<Coordinate> onCellDragDropped;   // NEW: drop released on a cell

    /**
     * Builds an empty board view (all water) with its labels.
     *
     * @param revealShips whether ship cells should be drawn visible
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
        for (int column = 0; column < Board.SIZE; column++) {
            Label label = new Label(String.valueOf((char) ('A' + column)));
            label.setPrefSize(CELL_SIZE, CELL_SIZE);
            label.setTextFill(Color.web("#1b4f72"));
            label.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            add(label, column + 1, 0);
        }
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

                cellView.setOnMouseClicked(event -> {
                    if (onCellClick != null) {
                        onCellClick.accept(coordinate);
                    }
                });

                // Allow this cell to be a drop target and report the hover.
                cellView.setOnDragOver(event -> {
                    if (onCellDragOver != null) {
                        onCellDragOver.accept(coordinate);
                    }
                    // Accept the move so the drop event can fire.
                    if (event.getGestureSource() != null) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                // Report a drop released on this cell.
                cellView.setOnDragDropped(event -> {
                    if (onCellDragDropped != null) {
                        onCellDragDropped.accept(coordinate);
                    }
                    event.setDropCompleted(true);
                    event.consume();
                });

                cellViews.put(coordinate, cellView);
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
     * Sets the listener invoked when the user clicks a cell (HU-2).
     *
     * @param onCellClick the click listener
     */
    public void setOnCellClick(Consumer<Coordinate> onCellClick) {
        this.onCellClick = onCellClick;
    }

    /**
     * Sets the listener invoked while a drag hovers over a cell (HU-1 placement).
     *
     * @param onCellDragOver the drag-over listener
     */
    public void setOnCellDragOver(Consumer<Coordinate> onCellDragOver) {
        this.onCellDragOver = onCellDragOver;
    }

    /**
     * Sets the listener invoked when a drag is dropped on a cell (HU-1 placement).
     *
     * @param onCellDragDropped the drop listener
     */
    public void setOnCellDragDropped(Consumer<Coordinate> onCellDragDropped) {
        this.onCellDragDropped = onCellDragDropped;
    }

    /**
     * Changes whether ships are drawn visible and redraws (HU-3).
     *
     * @param revealShips the new visibility for ship cells
     * @param board       the board to redraw with the new setting
     */
    public void setRevealShips(boolean revealShips, Board board) {
        this.revealShips = revealShips;
        render(board);
    }
}