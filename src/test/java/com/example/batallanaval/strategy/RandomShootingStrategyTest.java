package com.example.batallanaval.strategy;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.ShotResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link RandomShootingStrategy}.
 * <p>
 * Uses a seeded {@link Random} for reproducibility and checks the core HU-4 rule:
 * the machine never shoots the same cell twice.
 *
 */
class RandomShootingStrategyTest {

    @Test
    @DisplayName("The strategy never returns the same target twice")
    void neverRepeatsATarget() {
        RandomShootingStrategy strategy = new RandomShootingStrategy(new Random(42));
        Set<Coordinate> seen = new HashSet<>();

        // Ask for 50 targets, registering each so it is marked as used.
        for (int i = 0; i < 50; i++) {
            Coordinate target = strategy.chooseTarget();

            // If it were a repeat, add() would return false.
            assertTrue(seen.add(target), "Target was repeated: " + target);

            strategy.registerResult(target, ShotResult.WATER);
        }

        assertEquals(50, seen.size());
    }

    @Test
    @DisplayName("Every chosen target is inside the 10x10 board")
    void targetsAreWithinBounds() {
        RandomShootingStrategy strategy = new RandomShootingStrategy(new Random(123));

        for (int i = 0; i < 50; i++) {
            Coordinate target = strategy.chooseTarget();

            int row = target.getRow();
            int column = target.getColumn();
            assertTrue(row >= 0 && row < Board.SIZE
                            && column >= 0 && column < Board.SIZE,
                    "Target out of bounds: " + target);

            strategy.registerResult(target, ShotResult.WATER);
        }
    }

    @Test
    @DisplayName("The strategy can cover the entire board (100 distinct cells)")
    void canCoverWholeBoard() {
        RandomShootingStrategy strategy = new RandomShootingStrategy(new Random(7));
        Set<Coordinate> seen = new HashSet<>();

        // Board.SIZE * Board.SIZE = 100 cells in total.
        for (int i = 0; i < Board.SIZE * Board.SIZE; i++) {
            Coordinate target = strategy.chooseTarget();
            seen.add(target);
            strategy.registerResult(target, ShotResult.WATER);
        }

        assertEquals(100, seen.size());
        // A 101st request would loop forever, so we do NOT ask for one here.
        assertFalse(seen.isEmpty());
    }
}