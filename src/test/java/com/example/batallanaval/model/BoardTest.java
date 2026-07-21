package com.example.batallanaval.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Board} class.
 * <p>
 * Covers ship placement validation (HU-1) and shot resolution into
 * water / hit / sunk (HU-2), which is the core of the gameplay.
 *
 */
class BoardTest {

    @Test
    @DisplayName("A valid ship is placed and its cells become SHIP")
    void placesValidShip() throws InvalidPlacementException {
        Board board = new Board();
        Ship destroyer = new Ship(ShipType.DESTROYER, Set.of(
                new Coordinate(0, 0),
                new Coordinate(0, 1)));

        board.placeShip(destroyer);

        assertEquals(CellState.SHIP, board.getCellState(new Coordinate(0, 0)));
        assertEquals(CellState.SHIP, board.getCellState(new Coordinate(0, 1)));
    }

    @Test
    @DisplayName("Placing a ship off the board throws InvalidPlacementException")
    void rejectsShipOffBoard() {
        Board board = new Board();
        // Column 10 does not exist on a 0..9 board.
        Ship frigate = new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 10)));

        assertThrows(InvalidPlacementException.class, () -> board.placeShip(frigate));
    }

    @Test
    @DisplayName("Placing a ship over another one throws InvalidPlacementException")
    void rejectsOverlappingShip() throws InvalidPlacementException {
        Board board = new Board();
        board.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(5, 5))));

        Ship overlapping = new Ship(ShipType.FRIGATE, Set.of(new Coordinate(5, 5)));
        assertThrows(InvalidPlacementException.class, () -> board.placeShip(overlapping));
    }

    @Test
    @DisplayName("A shot on an empty cell returns WATER and marks it MISS")
    void shotOnWaterReturnsWater() {
        Board board = new Board();

        ShotResult result = board.receiveShot(new Coordinate(9, 9));

        assertEquals(ShotResult.WATER, result);
        assertEquals(CellState.MISS, board.getCellState(new Coordinate(9, 9)));
    }

    @Test
    @DisplayName("A shot on a multi-cell ship returns HIT for a partial hit")
    void shotOnShipReturnsHit() throws InvalidPlacementException {
        Board board = new Board();
        board.placeShip(new Ship(ShipType.DESTROYER, Set.of(
                new Coordinate(3, 3),
                new Coordinate(3, 4))));

        ShotResult result = board.receiveShot(new Coordinate(3, 3));

        assertEquals(ShotResult.HIT, result);
        assertEquals(CellState.HIT, board.getCellState(new Coordinate(3, 3)));
    }

    @Test
    @DisplayName("Sinking a ship returns SUNK and marks all its cells SUNK")
    void sinkingShipMarksAllCells() throws InvalidPlacementException {
        Board board = new Board();
        board.placeShip(new Ship(ShipType.DESTROYER, Set.of(
                new Coordinate(3, 3),
                new Coordinate(3, 4))));

        board.receiveShot(new Coordinate(3, 3));                 // HIT
        ShotResult result = board.receiveShot(new Coordinate(3, 4)); // sinks it

        assertEquals(ShotResult.SUNK, result);
        assertEquals(CellState.SUNK, board.getCellState(new Coordinate(3, 3)));
        assertEquals(CellState.SUNK, board.getCellState(new Coordinate(3, 4)));
    }

    @Test
    @DisplayName("Shooting the same cell twice throws IllegalStateException")
    void cannotShootSameCellTwice() {
        Board board = new Board();
        board.receiveShot(new Coordinate(1, 1));

        assertThrows(IllegalStateException.class,
                () -> board.receiveShot(new Coordinate(1, 1)));
    }

    @Test
    @DisplayName("allShipsSunk is true only when the whole fleet is sunk")
    void allShipsSunkDetectsVictory() throws InvalidPlacementException {
        Board board = new Board();
        board.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 0))));
        board.placeShip(new Ship(ShipType.FRIGATE, Set.of(new Coordinate(0, 1))));

        board.receiveShot(new Coordinate(0, 0)); // sinks first frigate
        assertFalse(board.allShipsSunk());        // one still afloat

        board.receiveShot(new Coordinate(0, 1)); // sinks second frigate
        assertTrue(board.allShipsSunk());         // whole fleet down
    }
}