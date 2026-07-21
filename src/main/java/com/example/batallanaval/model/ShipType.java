package com.example.batallanaval.model;

/**
 * The available ship types and the number of cells each one occupies.
 * <p>
 * The size is intrinsic to the type, so it lives here. This makes it impossible
 * to create a ship with an inconsistent size.
 *
 */
public enum ShipType {

    CARRIER(4),
    SUBMARINE(3),
    DESTROYER(2),
    FRIGATE(1);

    private final int size;

    /**
     * @param size the number of cells this ship type occupies
     */
    ShipType(int size) {
        this.size = size;
    }

    /**
     * @return the number of cells this ship type occupies
     */
    public int getSize() {
        return size;
    }
}