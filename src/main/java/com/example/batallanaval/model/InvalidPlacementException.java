package com.example.batallanaval.model;

/**
 * Checked exception thrown when a ship cannot be placed on the board because it
 * would fall outside the grid or overlap another ship (see HU-1).
 *
 */
public class InvalidPlacementException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param message a description of why the placement is invalid
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}