package com.example.batallanaval.factory;

import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ShipFactory}.
 * <p>
 * Focuses on the coordinate calculation (the classic off-by-one risk) and on the
 * composition of the standard fleet required by the statement.
 *
 */
class ShipFactoryTest {

    private final ShipFactory factory = new ShipFactory();

    @Test
    @DisplayName("A horizontal ship occupies consecutive columns on the same row")
    void horizontalShipHasCorrectCoordinates() {
        Ship destroyer = factory.createShip(
                ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);

        Set<Coordinate> coords = destroyer.getOccupiedCoordinates();
        assertEquals(2, coords.size());
        assertTrue(coords.contains(new Coordinate(0, 0)));
        assertTrue(coords.contains(new Coordinate(0, 1)));
    }

    @Test
    @DisplayName("A vertical ship occupies consecutive rows on the same column")
    void verticalShipHasCorrectCoordinates() {
        Ship destroyer = factory.createShip(
                ShipType.DESTROYER, new Coordinate(0, 0), Orientation.VERTICAL);

        Set<Coordinate> coords = destroyer.getOccupiedCoordinates();
        assertEquals(2, coords.size());
        assertTrue(coords.contains(new Coordinate(0, 0)));
        assertTrue(coords.contains(new Coordinate(1, 0)));
    }

    @Test
    @DisplayName("A carrier generates exactly 4 coordinates")
    void carrierHasFourCells() {
        Ship carrier = factory.createShip(
                ShipType.CARRIER, new Coordinate(2, 3), Orientation.HORIZONTAL);

        assertEquals(4, carrier.getOccupiedCoordinates().size());
    }

    @Test
    @DisplayName("The standard fleet has 10 ships with the exact 1/2/3/4 composition")
    void standardFleetHasCorrectComposition() {
        Queue<ShipType> fleet = factory.standardFleetTypes();

        assertEquals(10, fleet.size());

        // Count how many of each type the fleet contains.
        Map<ShipType, Integer> counts = new HashMap<>();
        for (ShipType type : fleet) {
            counts.merge(type, 1, Integer::sum);
        }

        assertEquals(1, counts.get(ShipType.CARRIER));
        assertEquals(2, counts.get(ShipType.SUBMARINE));
        assertEquals(3, counts.get(ShipType.DESTROYER));
        assertEquals(4, counts.get(ShipType.FRIGATE));
    }

    @Test
    @DisplayName("createShip rejects null arguments")
    void createShipRejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createShip(null, new Coordinate(0, 0), Orientation.HORIZONTAL));
    }
}