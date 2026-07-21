package com.example.batallanaval.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Coordinate} class.
 * <p>
 * The focus is the equals/hashCode contract, because Coordinate is used as the key
 * of the board Map. If that contract is broken, the whole board breaks silently.
 *
 */
class CoordinateTest {

    @Test
    @DisplayName("Constructor stores row and column and getters return them")
    void constructorAndGetters() {
        Coordinate coordinate = new Coordinate(3, 5);

        assertEquals(3, coordinate.getRow());
        assertEquals(5, coordinate.getColumn());
    }

    @Test
    @DisplayName("Two coordinates with the same row and column are equal")
    void equalCoordinatesAreEqual() {
        Coordinate a = new Coordinate(3, 5);
        Coordinate b = new Coordinate(3, 5);

        // Different objects in memory, but equal in value.
        assertEquals(a, b);
    }

    @Test
    @DisplayName("Equal coordinates produce the same hash code")
    void equalCoordinatesHaveSameHashCode() {
        Coordinate a = new Coordinate(7, 2);
        Coordinate b = new Coordinate(7, 2);

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Coordinates with different row or column are not equal")
    void differentCoordinatesAreNotEqual() {
        Coordinate base = new Coordinate(3, 5);

        assertNotEquals(base, new Coordinate(3, 6)); // different column
        assertNotEquals(base, new Coordinate(4, 5)); // different row
    }

    @Test
    @DisplayName("A coordinate is not equal to null or to an object of another class")
    void notEqualToNullOrOtherType() {
        Coordinate coordinate = new Coordinate(1, 1);

        assertNotEquals(coordinate, null);
        assertNotEquals(coordinate, "1,1");
    }

    @Test
    @DisplayName("Coordinate works correctly as a HashMap key")
    void worksAsMapKey() {
        Map<Coordinate, String> board = new HashMap<>();
        board.put(new Coordinate(4, 8), "SHIP");

        // Query with a DIFFERENT instance that has the same value.
        assertEquals("SHIP", board.get(new Coordinate(4, 8)));
        assertNull(board.get(new Coordinate(0, 0)));
    }

    @Test
    @DisplayName("Coordinate works correctly inside a HashSet (no duplicates)")
    void worksInsideSet() {
        Set<Coordinate> shots = new HashSet<>();
        shots.add(new Coordinate(2, 2));
        shots.add(new Coordinate(2, 2)); // same value, should NOT be added again

        assertEquals(1, shots.size());
        assertTrue(shots.contains(new Coordinate(2, 2)));
    }
}