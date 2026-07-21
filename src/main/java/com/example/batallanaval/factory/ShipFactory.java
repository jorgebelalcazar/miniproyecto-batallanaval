package com.example.batallanaval.factory;

import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Factory responsible for creating {@link Ship} objects.
 * <p>
 * This implements the Factory design pattern (creational): it centralizes and hides
 * the construction logic of a ship, in particular how a starting coordinate, an
 * {@link Orientation} and the ship's size are turned into the exact set of occupied
 * coordinates. Clients (the human placement controller and the machine AI) just ask
 * the factory for a ship and never duplicate this logic.
 *
 */
public class ShipFactory {

    /**
     * Creates a ship of the given type, starting at the given coordinate and extending
     * in the given orientation.
     *
     * @param type        the ship type (defines the size)
     * @param start       the top-left coordinate where the ship begins
     * @param orientation whether the ship extends horizontally or vertically
     * @return a new {@link Ship} occupying the computed coordinates
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public Ship createShip(ShipType type, Coordinate start, Orientation orientation) {
        if (type == null || start == null || orientation == null) {
            throw new IllegalArgumentException(
                    "Type, start and orientation cannot be null");
        }

        Set<Coordinate> coordinates = new LinkedHashSet<>();
        for (int i = 0; i < type.getSize(); i++) {
            if (orientation == Orientation.HORIZONTAL) {
                coordinates.add(new Coordinate(start.getRow(), start.getColumn() + i));
            } else {
                coordinates.add(new Coordinate(start.getRow() + i, start.getColumn()));
            }
        }
        return new Ship(type, coordinates);
    }

    /**
     * Returns the composition of the standard fleet as required by the statement:
     * 1 carrier, 2 submarines, 3 destroyers and 4 frigates (10 ships total).
     * <p>
     * A {@link Queue} is used here, which is the fourth required data structure.
     *
     * @return a queue with the 10 ship types to be placed, in a sensible order
     */
    public Queue<ShipType> standardFleetTypes() {
        Queue<ShipType> fleet = new ArrayDeque<>();
        fleet.add(ShipType.CARRIER);      // 1 x 4 cells
        fleet.add(ShipType.SUBMARINE);    // 2 x 3 cells
        fleet.add(ShipType.SUBMARINE);
        fleet.add(ShipType.DESTROYER);    // 3 x 2 cells
        fleet.add(ShipType.DESTROYER);
        fleet.add(ShipType.DESTROYER);
        fleet.add(ShipType.FRIGATE);      // 4 x 1 cell
        fleet.add(ShipType.FRIGATE);
        fleet.add(ShipType.FRIGATE);
        fleet.add(ShipType.FRIGATE);
        return fleet;
    }
}