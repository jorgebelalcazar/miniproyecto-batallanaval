package com.example.batallanaval.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an immutable position on the board, identified by a row and a column.
 * <p>
 * This class is used as the key of the board {@code Map<Coordinate, Cell>}, so it
 * overrides {@link #equals(Object)} and {@link #hashCode()} to guarantee that two
 * coordinates with the same row and column are treated as equal.
 * <p>
 * It implements {@link Serializable} because it will be part of the game state that
 * gets persisted to a serialized file later on.
 *
 */
public final class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int column;

    /**
     * Creates a coordinate with the given row and column.
     *
     * @param row    the row index (0-based)
     * @param column the column index (0-based)
     */
    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return the row index of this coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index of this coordinate
     */
    public int getColumn() {
        return column;
    }

    /**
     * Two coordinates are equal when they share the same row and column.
     * Required so this class can safely be used as a key in a HashMap/HashSet.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinate other = (Coordinate) obj;
        return row == other.row && column == other.column;
    }

    /**
     * Hash code consistent with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "Coordinate{row=" + row + ", column=" + column + "}";
    }
}