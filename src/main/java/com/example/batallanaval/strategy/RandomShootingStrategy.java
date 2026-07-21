package com.example.batallanaval.strategy;

import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.ShotResult;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A {@link ShootingStrategy} that shoots at random cells, never repeating one (HU-4).
 * <p>
 * It keeps a {@link Set} of the coordinates it has already fired at, and picks a new
 * random coordinate that is not in that set.
 *
 */
public class RandomShootingStrategy implements ShootingStrategy {

    private final Set<Coordinate> firedShots;
    private final Random random;

    /**
     * Creates a random strategy with its own random source.
     */
    public RandomShootingStrategy() {
        this(new Random());
    }

    /**
     * Creates a random strategy with an injected {@link Random}, useful for testing.
     *
     * @param random the random source to use
     */
    public RandomShootingStrategy(Random random) {
        this.firedShots = new HashSet<>();
        this.random = random;
    }

    @Override
    public Coordinate chooseTarget() {
        Coordinate candidate;
        do {
            candidate = new Coordinate(
                    random.nextInt(Board.SIZE), random.nextInt(Board.SIZE));
        } while (firedShots.contains(candidate));
        return candidate;
    }

    @Override
    public void registerResult(Coordinate coordinate, ShotResult result) {
        // The random strategy only needs to remember the cell has been used.
        firedShots.add(coordinate);
    }
}