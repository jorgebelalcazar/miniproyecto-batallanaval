package com.example.batallanaval.view;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.CellState;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Ship;

import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Renders a full 10x10 board as a grid of {@link CellView} objects, with column
 * labels (A-J) and row labels (1-10). It reports clicks and drag events and, for
 * visible ships, connects contiguous hull cells so ships look like one piece.
 *
 */
public class BoardView extends GridPane {

    private static final double CELL_SIZE = 34;

    private final Map<Coordinate, CellView> cellViews;
    private boolean revealShips;
    private Consumer<Coordinate> onCellClick;
    private Consumer<Coordinate> onCellDragOver;
    private Consumer<Coordinate> onCellDragDropped;

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

                cellView.setOnDragOver(event -> {
                    if (onCellDragOver != null) {
                        onCellDragOver.accept(coordinate);
                    }
                    if (event.getGestureSource() != null) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

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
     * Redraws every cell to match the given board's current state, connecting the
     * hulls of visible multi-cell ships.
     *
     * @param board the model board to reflect
     */
    public void render(Board board) {
        for (Map.Entry<Coordinate, CellView> entry : cellViews.entrySet()) {
            Coordinate coordinate = entry.getKey();
            CellState state = board.getCellState(coordinate);
            CellView.HullConnections connections =
                    connectionsFor(board, coordinate, state);
            entry.getValue().render(state, revealShips, connections);
        }
    }

    /**
     * Computes which sides of a cell connect to the same ship, so its hull can be drawn
     * continuously. Returns NONE for water, or when ships are hidden (so the enemy
     * fleet shape is never revealed through the hull connections).
     */
    private CellView.HullConnections connectionsFor(
            Board board, Coordinate coordinate, CellState state) {

        boolean isShip = state == CellState.SHIP
                || state == CellState.HIT
                || state == CellState.SUNK;
        if (!isShip || !revealShips) {
            return CellView.HullConnections.NONE;
        }

        Ship ship = findShipAt(board, coordinate);
        if (ship == null) {
            return CellView.HullConnections.NONE;
        }

        int r = coordinate.getRow();
        int col = coordinate.getColumn();
        return new CellView.HullConnections(
                ship.occupies(new Coordinate(r - 1, col)),   // top
                ship.occupies(new Coordinate(r + 1, col)),   // bottom
                ship.occupies(new Coordinate(r, col - 1)),   // left
                ship.occupies(new Coordinate(r, col + 1)));  // right
    }

    private Ship findShipAt(Board board, Coordinate coordinate) {
        for (Ship ship : board.getFleet()) {
            if (ship.occupies(coordinate)) {
                return ship;
            }
        }
        return null;
    }

    /**
     * Sets the click listener (HU-2).
     *
     * @param onCellClick the click listener
     */
    public void setOnCellClick(Consumer<Coordinate> onCellClick) {
        this.onCellClick = onCellClick;
    }

    /**
     * Sets the drag-over listener (HU-1 placement).
     *
     * @param onCellDragOver the drag-over listener
     */
    public void setOnCellDragOver(Consumer<Coordinate> onCellDragOver) {
        this.onCellDragOver = onCellDragOver;
    }

    /**
     * Sets the drop listener (HU-1 placement).
     *
     * @param onCellDragDropped the drop listener
     */
    public void setOnCellDragDropped(Consumer<Coordinate> onCellDragDropped) {
        this.onCellDragDropped = onCellDragDropped;
    }

    /**
     * Changes ship visibility and redraws (HU-3).
     *
     * @param revealShips the new visibility for ship cells
     * @param board       the board to redraw with the new setting
     */
    public void setRevealShips(boolean revealShips, Board board) {
        this.revealShips = revealShips;
        render(board);
    }
}