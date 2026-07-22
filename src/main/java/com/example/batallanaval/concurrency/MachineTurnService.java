package com.example.batallanaval.concurrency;

import com.example.batallanaval.model.Player;
import com.example.batallanaval.model.ShotResult;
import com.example.batallanaval.service.GameManager;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.Consumer;

/**
 * Runs the machine's turn on a background thread so the UI never freezes (criterion 7).
 * <p>
 * The machine keeps shooting while it hits, pausing briefly between shots so the turn
 * feels natural. Each shot result is reported back to the UI through a callback, which
 * the controller must dispatch to the JavaFX thread with {@code Platform.runLater}.
 *
 */
public class MachineTurnService extends Service<Void> {

    /** Delay between machine shots, in milliseconds, so the turn is watchable. */
    private static final long DELAY_BETWEEN_SHOTS_MS = 700;

    private final GameManager game;
    private Consumer<ShotResult> onShot;

    /**
     * @param game the game whose machine turn will be played
     */
    public MachineTurnService(GameManager game) {
        this.game = game;
    }

    /**
     * Sets the callback invoked after each machine shot. The callback runs on a
     * background thread, so any UI update inside it must use {@code Platform.runLater}.
     *
     * @param onShot receives the result of each machine shot
     */
    public void setOnShot(Consumer<ShotResult> onShot) {
        this.onShot = onShot;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                // Keep shooting while it is the machine's turn and the game is on.
                while (game.getCurrentTurn() == Player.MACHINE && !game.isGameOver()) {
                    Thread.sleep(DELAY_BETWEEN_SHOTS_MS);   // simulate "thinking"
                    ShotResult result = game.machineShoot();
                    if (onShot != null) {
                        onShot.accept(result);
                    }
                }
                return null;
            }
        };
    }
}