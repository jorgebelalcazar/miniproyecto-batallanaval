package com.example.batallanaval.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Ship} class.
 * <p>
 * Focuses on the two rules that matter most: the size validation in the constructor
 * and the sink detection ({@code isSunk}), which is the game's win condition.
 *
 */
class ShipTest {

    @Test
    @DisplayName("Constructor rejects a coordinate set whose size does not match the type")
    void constructorRejectsWrongSize() {
        // A DESTROYER must occupy 2 cells; giving it 3 must fail.
        Set<Coordinate> wrongSize = Set.of(
                new Coordinate(0, 0),
                new Coordinate(0, 1),
                new Coordinate(0, 2));

        assertThrows(IllegalArgumentException.class,
                () -> new Ship(ShipType.DESTROYER, wrongSize));
    }

    @Test
    @DisplayName("A ship is not sunk until every one of its cells has been hit")
    void shipIsNotSunkUntilAllCellsAreHit() {
        // SUBMARINE occupies 3 cells.
        Ship submarine = new Ship(ShipType.SUBMARINE, Set.of(
                new Coordinate(2, 0),
                new Coordinate(2, 1),
                new Coordinate(2, 2)));

        assertFalse(submarine.isSunk());              // no hits yet

        submarine.registerHit(new Coordinate(2, 0));
        submarine.registerHit(new Coordinate(2, 1));
        assertFalse(submarine.isSunk());              // 2 of 3 hit, still afloat

        submarine.registerHit(new Coordinate(2, 2));
        assertTrue(submarine.isSunk());               // all 3 hit, sunk
    }

    @Test
    @DisplayName("registerHit returns true on an occupied cell and false otherwise")
    void registerHitReturnsWhetherItHit() {
        Ship destroyer = new Ship(ShipType.DESTROYER, Set.of(
                new Coordinate(5, 5),
                new Coordinate(5, 6)));

        assertTrue(destroyer.registerHit(new Coordinate(5, 5)));   // occupied -> hit
        assertFalse(destroyer.registerHit(new Coordinate(9, 9)));  // not occupied -> miss
    }

    @Test
    @DisplayName("A frigate (size 1) is sunk by a single hit")
    void frigateSinksWithOneHit() {
        Ship frigate = new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 0)));

        assertFalse(frigate.isSunk());
        frigate.registerHit(new Coordinate(0, 0));
        assertTrue(frigate.isSunk());
    }
}