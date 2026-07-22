package com.example.batallanaval.strategy;

import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.ShotResult;

import java.io.Serializable;

/**
 * Strategy for deciding where the machine shoots next (see HU-4).
 * <p>
 * Extends {@link Serializable} so the machine's state can be saved together with the
 * whole game (HU-5): the {@link com.example.batallanaval.service.GameManager} holds a
 * strategy, and the entire game is serialized to resume it later.
 *
 */
public interface ShootingStrategy extends Serializable {

    Coordinate chooseTarget();

    void registerResult(Coordinate coordinate, ShotResult result);
}