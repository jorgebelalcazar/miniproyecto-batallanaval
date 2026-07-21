package com.example.batallanaval;

import com.example.batallanaval.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point of the Battleship (Batalla Naval) application.
 * <p>
 * This class bootstraps the JavaFX runtime and delegates all view loading
 * to the {@link ViewManager}. Following a strict Model-View-Controller
 * design, it does not build any UI component by hand: it only starts the
 * JavaFX lifecycle and asks the view manager to display the initial screen.
 * </p>
 *
 * @author Jorge Iván Belalcázar
 * @version 1.0.0
 * @since 2026-07
 */
public class App extends Application {

    /**
     * JavaFX entry point. Stores the primary stage in the view manager and
     * requests the initial start screen. No UI elements are created here.
     *
     * @param primaryStage the primary window provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {
        ViewManager.getInstance().initialize(primaryStage);
        ViewManager.getInstance().showStartView();
    }

    /**
     * Standard Java entry point. Delegates control to the JavaFX runtime.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}