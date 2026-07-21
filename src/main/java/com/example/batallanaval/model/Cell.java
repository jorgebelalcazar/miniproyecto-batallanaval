package com.example.batallanaval.model;

import java.io.Serializable;

/**
 * Represents a single square on a board. A cell only knows its own {@link CellState};
 * it does not know how it is drawn (that is the View's responsibility) nor the rules
 * of the game (that is the game logic's responsibility).
 * <p>
 * Implements {@link Serializable} so the full board can be saved to a serialized file
 * during persistence.
 *
 */
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    private CellState state;

    /**
     * Creates an empty cell (water, not shot at).
     */
    public Cell() {
        this.state = CellState.EMPTY;
    }

    /**
     * @return the current state of this cell
     */
    public CellState getState() {
        return state;
    }

    /**
     * Updates the state of this cell.
     *
     * @param state the new state; must not be {@code null}
     */
    public void setState(CellState state) {
        if (state == null) {
            throw new IllegalArgumentException("Cell state cannot be null");
        }
        this.state = state;
    }

    /**
     * @return {@code true} if this cell holds part of a ship, regardless of whether
     *         it has been hit or sunk
     */
    public boolean isShipPart() {
        return state == CellState.SHIP
                || state == CellState.HIT
                || state == CellState.SUNK;
    }

    /**
     * @return {@code true} if this cell has already been shot at
     */
    public boolean hasBeenShot() {
        return state == CellState.MISS
                || state == CellState.HIT
                || state == CellState.SUNK;
    }

    @Override
    public String toString() {
        return "Cell{state=" + state + "}";
    }
}