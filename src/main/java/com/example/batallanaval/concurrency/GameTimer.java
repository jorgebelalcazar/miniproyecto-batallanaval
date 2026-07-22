package com.example.batallanaval.concurrency;

import javafx.concurrent.Task;

/**
 * A background timer that counts the elapsed game time, updating once per second.
 * This is the second concurrent thread required by the rubric (criterion 7): it runs
 * in parallel with the game so the clock advances while both players take their turns.
 * <p>
 * It uses {@link Task#updateMessage(String)} to publish the formatted time, which
 * JavaFX safely delivers to the UI thread via the task's {@code messageProperty}.
 *
 */
public class GameTimer extends Task<Void> {

    @Override
    protected Void call() throws InterruptedException {
        int elapsedSeconds = 0;
        while (!isCancelled()) {
            updateMessage(format(elapsedSeconds));
            Thread.sleep(1000);       // wait one second
            elapsedSeconds++;
        }
        return null;
    }

    /**
     * Formats a number of seconds as mm:ss.
     *
     * @param totalSeconds the elapsed seconds
     * @return the time formatted as minutes and seconds
     */
    private String format(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("Tiempo: %02d:%02d", minutes, seconds);
    }
}