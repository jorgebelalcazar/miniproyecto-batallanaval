package com.example.batallanaval.strategy;

import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.ShotResult;

/**
 * Strategy for deciding where the machine shoots next (see HU-4).
 * <p>
 * This is the abstraction of the Strategy design pattern: the game logic depends on
 * this interface, not on a concrete algorithm, so the machine's behaviour can be
 * swapped or extended without changing the game logic (SOLID: Open/Closed and
 * Dependency Inversion).
 *

 */
public interface ShootingStrategy {

    /**
     * Chooses the next coordinate to shoot at. Implementations must never return a
     * coordinate they have already chosen.
     *
     * @return the coordinate to shoot at
     */
    Coordinate chooseTarget();

    /**
     * Informs the strategy of the result of the shot it just chose, so smarter
     * strategies can react to it. The random strategy simply records that the cell
     * has been used.
     *
     * @param coordinate the coordinate that was shot
     * @param result     the outcome of that shot
     */
    void registerResult(Coordinate coordinate, ShotResult result);
}