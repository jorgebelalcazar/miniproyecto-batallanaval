package com.example.batallanaval.service;

import com.example.batallanaval.factory.ShipFactory;
import com.example.batallanaval.model.Board;
import com.example.batallanaval.model.Coordinate;
import com.example.batallanaval.model.InvalidPlacementException;
import com.example.batallanaval.model.Orientation;
import com.example.batallanaval.model.Ship;
import com.example.batallanaval.model.ShipType;

import java.util.Queue;
import java.util.Random;

/**
 * Places a full fleet randomly on a board, following the game rules (HU-4).
 * <p>
 * For each ship it repeatedly generates a random start coordinate and orientation,
 * then tries to place it. If the {@link Board} rejects the placement (off-board or
 * overlap) it retries with a new random position. This reuses the board's own
 * validation instead of duplicating "where does it fit" logic here.
 *
 */
public class RandomFleetPlacer {

    /** Safety limit of placement attempts per ship before restarting the board. */
    private static final int MAX_ATTEMPTS_PER_SHIP = 500;

    private final ShipFactory shipFactory;
    private final Random random;

    /**
     * Creates a placer with its own random source.
     */
    public RandomFleetPlacer() {
        this.shipFactory = new ShipFactory();
        this.random = new Random();
    }

    /**
     * Creates a placer with an injected {@link Random}, useful for testing with a seed.
     *
     * @param random the random source to use
     */
    public RandomFleetPlacer(Random random) {
        this.shipFactory = new ShipFactory();
        this.random = random;
    }

    /**
     * Creates a new board and places the standard fleet (1 carrier, 2 submarines,
     * 3 destroyers, 4 frigates) randomly on it.
     *
     * @return a board with all 10 ships placed without overlaps
     */
    public Board createBoardWithRandomFleet() {
        while (true) {
            Board board = new Board();
            if (tryPlaceWholeFleet(board)) {
                return board;
            }
            // If a ship could not be placed after many attempts, start over.
        }
    }

    /**
     * Attempts to place the whole standard fleet on the given board.
     *
     * @param board the board to fill
     * @return {@code true} if every ship was placed, {@code false} if one failed
     */
    private boolean tryPlaceWholeFleet(Board board) {
        Queue<ShipType> types = shipFactory.standardFleetTypes();
        while (!types.isEmpty()) {
            ShipType type = types.poll();
            if (!tryPlaceSingleShip(board, type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to place a single ship of the given type at random positions, up to the
     * attempt limit.
     *
     * @param board the board to place on
     * @param type  the type of ship to place
     * @return {@code true} if it was placed, {@code false} if the limit was reached
     */
    private boolean tryPlaceSingleShip(Board board, ShipType type) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_SHIP; attempt++) {
            Coordinate start = new Coordinate(
                    random.nextInt(Board.SIZE), random.nextInt(Board.SIZE));
            Orientation orientation = random.nextBoolean()
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;

            Ship ship = shipFactory.createShip(type, start, orientation);
            try {
                board.placeShip(ship);
                return true;              // placed successfully
            } catch (InvalidPlacementException e) {
                // Position not valid (off-board or overlap): try another one.
            }
        }
        return false;                     // could not place after MAX_ATTEMPTS
    }
}