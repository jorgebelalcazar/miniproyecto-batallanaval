package com.example.batallanaval.service;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.Ship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link RandomFleetPlacer}.
 * <p>
 * Uses a seeded {@link Random} so the random placement is reproducible, and checks
 * that the resulting fleet is complete, non-overlapping and within bounds (HU-4).
 *
 */
class RandomFleetPlacerTest {

    @Test
    @DisplayName("The generated board contains exactly the 10 ships of the fleet")
    void boardHasTenShips() {
        RandomFleetPlacer placer = new RandomFleetPlacer(new Random(42));

        Board board = placer.createBoardWithRandomFleet();

        assertEquals(10, board.getFleet().size());
    }

    @Test
    @DisplayName("No two ships overlap: all occupied cells are distinct (20 total)")
    void shipsDoNotOverlap() {
        RandomFleetPlacer placer = new RandomFleetPlacer(new Random(42));

        Board board = placer.createBoardWithRandomFleet();

        // Collect every occupied coordinate into a Set; duplicates would collapse.
        Set<Coordinate> occupied = new HashSet<>();
        for (Ship ship : board.getFleet()) {
            occupied.addAll(ship.getOccupiedCoordinates());
        }

        // 1x4 + 2x3 + 3x2 + 4x1 = 20 cells. If any overlapped, the set would be smaller.
        assertEquals(20, occupied.size());
    }

    @Test
    @DisplayName("Every occupied cell is inside the 10x10 board")
    void allShipsAreWithinBounds() {
        RandomFleetPlacer placer = new RandomFleetPlacer(new Random(123));

        Board board = placer.createBoardWithRandomFleet();

        for (Ship ship : board.getFleet()) {
            for (Coordinate coordinate : ship.getOccupiedCoordinates()) {
                assertTrue(isWithinBounds(coordinate),
                        "Coordinate out of bounds: " + coordinate);
            }
        }
    }

    @Test
    @DisplayName("The same seed produces the same fleet layout (reproducible)")
    void sameSeedProducesSameLayout() {
        Board first = new RandomFleetPlacer(new Random(7)).createBoardWithRandomFleet();
        Board second = new RandomFleetPlacer(new Random(7)).createBoardWithRandomFleet();

        Set<Coordinate> firstCells = occupiedCellsOf(first);
        Set<Coordinate> secondCells = occupiedCellsOf(second);

        assertEquals(firstCells, secondCells);
    }

    private boolean isWithinBounds(Coordinate coordinate) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        return row >= 0 && row < Board.SIZE && column >= 0 && column < Board.SIZE;
    }

    private Set<Coordinate> occupiedCellsOf(Board board) {
        Set<Coordinate> occupied = new HashSet<>();
        for (Ship ship : board.getFleet()) {
            occupied.addAll(ship.getOccupiedCoordinates());
        }
        return occupied;
    }
}