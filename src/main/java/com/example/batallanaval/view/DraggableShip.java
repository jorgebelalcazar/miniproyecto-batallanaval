package com.example.batallanaval.view;

import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.ShipType;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A ship shown in the placement tray, drawn with JavaFX shapes (criterion 11). Its
 * length reflects the ship type's size. It knows which {@link ShipType} it represents
 * and its current {@link Orientation}; the drag behaviour is added by the controller.
 *
 */
public class DraggableShip extends StackPane {

    /** Pixel size of one cell, matching the board cell size. */
    private static final double CELL_SIZE = 34;

    private final ShipType type;
    private Orientation orientation;
    private final Rectangle body;

    /**
     * Creates a draggable ship of the given type, initially horizontal.
     *
     * @param type the ship type this component represents
     */
    public DraggableShip(ShipType type) {
        this.type = type;
        this.orientation = Orientation.HORIZONTAL;

        this.body = new Rectangle();
        body.setArcWidth(CELL_SIZE * 0.35);
        body.setArcHeight(CELL_SIZE * 0.35);
        body.setFill(Color.web("#4a5560"));
        body.setStroke(Color.web("#2c343c"));
        body.setStrokeWidth(1.5);

        getChildren().add(body);
        applyOrientation();
    }

    /**
     * @return the ship type represented by this component
     */
    public ShipType getShipType() {
        return type;
    }

    /**
     * @return the current orientation of this ship
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Flips the orientation between horizontal and vertical and redraws.
     */
    public void toggleOrientation() {
        orientation = (orientation == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        applyOrientation();
    }

    /**
     * Resizes the rectangle so its length matches the ship size along the current
     * orientation (a small inset keeps a visible gap between cells).
     */
    private void applyOrientation() {
        double inset = CELL_SIZE * 0.12;
        double longSide = CELL_SIZE * type.getSize() - 2 * inset;
        double shortSide = CELL_SIZE - 2 * inset;

        if (orientation == Orientation.HORIZONTAL) {
            body.setWidth(longSide);
            body.setHeight(shortSide);
        } else {
            body.setWidth(shortSide);
            body.setHeight(longSide);
        }
    }
}