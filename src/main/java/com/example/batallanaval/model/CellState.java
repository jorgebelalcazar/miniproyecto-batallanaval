package com.example.batallanaval.model;

/**
 * Represents the possible states of a single cell on the board.
 * <p>
 * The state combines two ideas: whether the cell holds part of a ship, and whether
 * it has already been shot at. Keeping this as an enum (instead of separate booleans)
 * makes the model clean and lets the View map each state directly to a 2D shape.
 *
 */
public enum CellState {

    /** Water that has not been shot at yet. */
    EMPTY,

    /** Contains part of a ship that has not been hit yet. */
    SHIP,

    /** Was shot at, but there was only water (a miss). */
    MISS,

    /** Was shot at and hit a ship that is not fully sunk yet. */
    HIT,

    /** Part of a ship that has been completely sunk. */
    SUNK
}