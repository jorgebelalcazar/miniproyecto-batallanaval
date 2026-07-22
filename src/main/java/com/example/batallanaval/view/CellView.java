package com.example.batallanaval.view;

import com.example.batallanaval.model.CellState;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * A single board cell drawn entirely with JavaFX Shapes (no images), as required by
 * the rubric (criterion 11). It renders water, ship, hit and sunk states.
 * <p>
 * For ship cells, it can connect its hull to neighbouring ship cells (see
 * {@link HullConnections}) so a multi-cell ship looks like one continuous piece.
 *
 */
public class CellView extends StackPane {

    private static final Color WATER_FILL = Color.web("#9fd3e8");
    private static final Color WATER_BORDER = Color.web("#6fb2cf");
    private static final Color SHIP_FILL = Color.web("#4a5560");
    private static final Color SHIP_BORDER = Color.web("#2c343c");
    private static final Color SUNK_FILL = Color.web("#3a2020");

    private final double size;

    /**
     * Which sides of this cell connect to another part of the same ship. Where a side
     * connects, the hull extends to that edge; where it does not, a rounded margin is
     * kept, so ship ends look rounded and middles look continuous.
     *
     * @param top    whether the ship continues upward
     * @param bottom whether the ship continues downward
     * @param left   whether the ship continues to the left
     * @param right  whether the ship continues to the right
     */
    public record HullConnections(boolean top, boolean bottom,
                                  boolean left, boolean right) {

        /** No connections (a single-cell ship or a hidden cell). */
        public static final HullConnections NONE =
                new HullConnections(false, false, false, false);
    }

    /**
     * Creates a square cell of the given pixel size, initially showing water.
     *
     * @param size the width and height of the cell in pixels
     */
    public CellView(double size) {
        this.size = size;
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);
        render(CellState.EMPTY, false, HullConnections.NONE);
    }

    /**
     * Draws this cell according to the given state and hull connections.
     *
     * @param state       the state of the cell in the model
     * @param revealShip  if {@code true}, a SHIP cell is drawn as a ship; if
     *                    {@code false}, it is hidden and drawn as water
     * @param connections which sides connect to the same ship (use
     *                    {@link HullConnections#NONE} when hidden or single-cell)
     */
    public void render(CellState state, boolean revealShip, HullConnections connections) {
        getChildren().clear();
        getChildren().add(waterBackground());

        switch (state) {
            case EMPTY:
                break;
            case SHIP:
                if (revealShip) {
                    getChildren().add(shipBody(SHIP_FILL, connections));
                }
                break;
            case MISS:
                getChildren().add(waterCross());
                break;
            case HIT:
                getChildren().add(shipBody(SHIP_FILL, connections));
                getChildren().add(flame(Color.ORANGE, Color.web("#c0392b"), 0.6));
                break;
            case SUNK:
                getChildren().add(shipBody(SUNK_FILL, connections));
                getChildren().add(flame(Color.web("#e74c3c"), Color.web("#7b1f1f"), 0.8));
                break;
        }
    }

    private Rectangle waterBackground() {
        Rectangle water = new Rectangle(size, size);
        water.setFill(WATER_FILL);
        water.setStroke(WATER_BORDER);
        water.setStrokeWidth(1);
        return water;
    }

    /**
     * Builds the hull rectangle. On sides that connect to another ship part the hull
     * reaches the cell edge (inset 0); on free sides it keeps a rounded margin. The
     * rectangle is translated so it grows toward the connected sides.
     */
    private Rectangle shipBody(Color fill, HullConnections c) {
        double margin = size * 0.12;

        double left = c.left() ? 0 : margin;
        double right = c.right() ? 0 : margin;
        double top = c.top() ? 0 : margin;
        double bottom = c.bottom() ? 0 : margin;

        double width = size - left - right;
        double height = size - top - bottom;

        Rectangle hull = new Rectangle(width, height);
        // Only round corners when the cell is a ship end (no connection on that axis).
        double arc = size * 0.35;
        hull.setArcWidth(c.left() || c.right() ? 0 : arc);
        hull.setArcHeight(c.top() || c.bottom() ? 0 : arc);
        hull.setFill(fill);
        hull.setStroke(SHIP_BORDER);
        hull.setStrokeWidth(1.5);

        // StackPane centers children; shift the hull so the grown sides reach the edge.
        hull.setTranslateX((left - right) / 2.0);
        hull.setTranslateY((top - bottom) / 2.0);
        return hull;
    }

    private javafx.scene.Group waterCross() {
        double inset = size * 0.28;
        Line a = new Line(inset, inset, size - inset, size - inset);
        Line b = new Line(size - inset, inset, inset, size - inset);
        for (Line line : new Line[]{a, b}) {
            line.setStroke(Color.web("#1b4f72"));
            line.setStrokeWidth(2.5);
        }
        return new javafx.scene.Group(a, b);
    }

    private javafx.scene.Group flame(Color outer, Color inner, double scale) {
        double c = size / 2.0;
        double h = size * 0.30 * scale;
        Polygon fire = new Polygon(
                c, c - h,
                c + h * 0.7, c + h * 0.6,
                c, c + h,
                c - h * 0.7, c + h * 0.6);
        fire.setFill(outer);
        Circle core = new Circle(c, c + h * 0.15, h * 0.4, inner);
        return new javafx.scene.Group(fire, core);
    }
}