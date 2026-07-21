package com.example.batallanaval.model;

/**
 * The possible outcomes of a shot on a board, following the game rules:
 * WATER passes the turn, HIT and SUNK let the player shoot again.
 *
 */
public enum ShotResult {

    /** The shot hit water (no ship). The turn passes to the opponent. */
    WATER,

    /** The shot hit a part of a ship that is not fully sunk yet. Shoot again. */
    HIT,

    /** The shot hit the last remaining cell of a ship, sinking it. Shoot again. */
    SUNK
}