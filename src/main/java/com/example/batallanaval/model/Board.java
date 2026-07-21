package com.example.batallanaval.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a 10x10 game board WITHOUT using an array or matrix.
 * <p>
 * It uses three data structures required by the rubric:
 * <ul>
 *   <li>{@code Map<Coordinate, Cell>} for the grid (instead of a 2D array),</li>
 *   <li>{@code Set<Ship>} for the fleet,</li>
 *   <li>{@code Set<Coordinate>} for the shots already fired.</li>
 * </ul>
 * The board knows how to place ships (HU-1) and how to resolve a shot into
 * water / hit / sunk (HU-2). It does NOT manage turns (that is game logic's job)
 * nor how it is drawn (that is the View's job).
 * <p>
 * Implements {@link Serializable} so the whole board can be saved (HU-5).
 *
 */
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The side length of the board (10x10). */
    public static final int SIZE = 10;

    private final Map<Coordinate, Cell> cells;
    private final Set<Ship> fleet;
    private final Set<Coordinate> shotsFired;

    /**
     * Creates an empty 10x10 board with every cell set to water (EMPTY).
     */
    public Board() {
        this.cells = new HashMap<>();
        this.fleet = new LinkedHashSet<>();
        this.shotsFired = new HashSet<>();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                cells.put(new Coordinate(row, column), new Cell());
            }
        }
    }

    /**
     * Places a ship on the board, validating that every coordinate is inside the
     * grid and not already occupied by another ship (HU-1).
     *
     * @param ship the ship to place
     * @throws InvalidPlacementException if the ship falls off the board or overlaps
     */
    public void placeShip(Ship ship) throws InvalidPlacementException {
        for (Coordinate coordinate : ship.getOccupiedCoordinates()) {
            if (!isWithinBounds(coordinate)) {
                throw new InvalidPlacementException(
                        "Ship goes off the board at " + coordinate);
            }
            if (cells.get(coordinate).isShipPart()) {
                throw new InvalidPlacementException(
                        "Ship overlaps another ship at " + coordinate);
            }
        }
        // All coordinates are valid: mark them and register the ship.
        for (Coordinate coordinate : ship.getOccupiedCoordinates()) {
            cells.get(coordinate).setState(CellState.SHIP);
        }
        fleet.add(ship);
    }

    /**
     * Resolves a shot at the given coordinate (HU-2).
     *
     * @param coordinate the target coordinate
     * @return the result of the shot: WATER, HIT or SUNK
     * @throws IllegalArgumentException if the coordinate is off the board
     * @throws IllegalStateException    if that coordinate was already shot at
     */
    public ShotResult receiveShot(Coordinate coordinate) {
        if (!isWithinBounds(coordinate)) {
            throw new IllegalArgumentException("Shot is off the board: " + coordinate);
        }
        if (shotsFired.contains(coordinate)) {
            throw new IllegalStateException("Cell already shot: " + coordinate);
        }
        shotsFired.add(coordinate);

        Cell cell = cells.get(coordinate);
        if (!cell.isShipPart()) {
            cell.setState(CellState.MISS);
            return ShotResult.WATER;
        }

        Ship hitShip = findShipAt(coordinate);
        hitShip.registerHit(coordinate);

        if (hitShip.isSunk()) {
            // Mark every cell of the sunk ship as SUNK.
            for (Coordinate part : hitShip.getOccupiedCoordinates()) {
                cells.get(part).setState(CellState.SUNK);
            }
            return ShotResult.SUNK;
        }
        cell.setState(CellState.HIT);
        return ShotResult.HIT;
    }

    /**
     * @return {@code true} when every ship of the fleet has been sunk (win condition)
     */
    public boolean allShipsSunk() {
        for (Ship ship : fleet) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return !fleet.isEmpty();
    }

    /**
     * @return the number of ships currently sunk (used for the flat file save, HU-5)
     */
    public int getSunkShipCount() {
        int count = 0;
        for (Ship ship : fleet) {
            if (ship.isSunk()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param coordinate the cell to query
     * @return the state of that cell, so the View can draw it
     */
    public CellState getCellState(Coordinate coordinate) {
        return cells.get(coordinate).getState();
    }

    /**
     * @return an unmodifiable view of the fleet
     */
    public Set<Ship> getFleet() {
        return Collections.unmodifiableSet(fleet);
    }

    private boolean isWithinBounds(Coordinate coordinate) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    private Ship findShipAt(Coordinate coordinate) {
        for (Ship ship : fleet) {
            if (ship.occupies(coordinate)) {
                return ship;
            }
        }
        throw new IllegalStateException("No ship found at " + coordinate);
    }
}