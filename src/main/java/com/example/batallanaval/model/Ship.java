package com.example.batallanaval.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single ship on the board.
 * <p>
 * A ship knows its {@link ShipType}, the set of coordinates it occupies, and the set
 * of coordinates that have already been hit. It does not know how it is drawn (View's
 * job) nor how it is placed on the board (the board/logic's job).
 * <p>
 * The occupied coordinates are stored in a {@link Set}, reinforcing the "no arrays"
 * requirement, and rely on {@link Coordinate}'s equals/hashCode contract.
 *
 */
public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ShipType type;
    private final Set<Coordinate> occupiedCoordinates;
    private final Set<Coordinate> hitCoordinates;

    /**
     * Creates a ship of the given type occupying the given coordinates.
     *
     * @param type        the type of the ship
     * @param coordinates the coordinates this ship occupies; its size must match the
     *                    type's size
     * @throws IllegalArgumentException if the number of coordinates does not match the
     *                                  ship type's size
     */
    public Ship(ShipType type, Set<Coordinate> coordinates) {
        if (type == null || coordinates == null) {
            throw new IllegalArgumentException("Type and coordinates cannot be null");
        }
        if (coordinates.size() != type.getSize()) {
            throw new IllegalArgumentException(
                    "A " + type + " must occupy exactly " + type.getSize() + " cells");
        }
        this.type = type;
        this.occupiedCoordinates = new HashSet<>(coordinates);
        this.hitCoordinates = new HashSet<>();
    }

    /**
     * @return the type of this ship
     */
    public ShipType getType() {
        return type;
    }

    /**
     * @return an unmodifiable view of the coordinates this ship occupies
     */
    public Set<Coordinate> getOccupiedCoordinates() {
        return Collections.unmodifiableSet(occupiedCoordinates);
    }

    /**
     * @return the number of cells this ship occupies
     */
    public int getSize() {
        return type.getSize();
    }

    /**
     * Checks whether this ship occupies the given coordinate.
     *
     * @param coordinate the coordinate to check
     * @return {@code true} if the ship occupies that coordinate
     */
    public boolean occupies(Coordinate coordinate) {
        return occupiedCoordinates.contains(coordinate);
    }

    /**
     * Registers a hit on the given coordinate, if the ship occupies it.
     *
     * @param coordinate the coordinate that was shot
     * @return {@code true} if the shot hit this ship, {@code false} otherwise
     */
    public boolean registerHit(Coordinate coordinate) {
        if (occupiedCoordinates.contains(coordinate)) {
            hitCoordinates.add(coordinate);
            return true;
        }
        return false;
    }

    /**
     * @return {@code true} when every coordinate of this ship has been hit
     */
    public boolean isSunk() {
        return hitCoordinates.size() == occupiedCoordinates.size();
    }

    @Override
    public String toString() {
        return "Ship{type=" + type
                + ", size=" + getSize()
                + ", hits=" + hitCoordinates.size() + "}";
    }
}