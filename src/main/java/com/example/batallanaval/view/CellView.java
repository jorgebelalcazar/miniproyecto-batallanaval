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
 * This is a pure View component: it receives a {@link CellState} and draws it. It does
 * not know the game rules nor the model internals.
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
     * Creates a square cell of the given pixel size, initially showing water.
     *
     * @param size the width and height of the cell in pixels
     */
    public CellView(double size) {
        this.size = size;
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);
        render(CellState.EMPTY, false);
    }

    /**
     * Draws this cell according to the given state.
     *
     * @param state      the state of the cell in the model
     * @param revealShip if {@code true}, a SHIP cell is drawn as a ship; if
     *                   {@code false}, it is hidden and drawn as water (used for the
     *                   enemy board)
     */
    public void render(CellState state, boolean revealShip) {
        getChildren().clear();
        getChildren().add(waterBackground());

        switch (state) {
            case EMPTY:
                break; // only water
            case SHIP:
                if (revealShip) {
                    getChildren().add(shipBody(SHIP_FILL));
                }
                break;
            case MISS:
                getChildren().add(waterCross()); // the "X" for water (HU-2)
                break;
            case HIT:
                getChildren().add(shipBody(SHIP_FILL));
                getChildren().add(flame(Color.ORANGE, Color.web("#c0392b"), 0.6));
                break;
            case SUNK:
                getChildren().add(shipBody(SUNK_FILL));
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

    private Rectangle shipBody(Color fill) {
        double inset = size * 0.12;
        Rectangle hull = new Rectangle(size - 2 * inset, size - 2 * inset);
        hull.setArcWidth(size * 0.35);   // rounded corners for a hull look
        hull.setArcHeight(size * 0.35);
        hull.setFill(fill);
        hull.setStroke(SHIP_BORDER);
        hull.setStrokeWidth(1.5);
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
        // A simple flame/impact shape built from a triangle-ish polygon.
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