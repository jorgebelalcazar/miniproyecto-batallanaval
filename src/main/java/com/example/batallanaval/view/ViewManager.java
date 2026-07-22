package com.example.batallanaval.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Centralized manager responsible for loading FXML views and swapping scenes on the
 * primary stage. It is the single point of contact with the JavaFX {@link Stage}.
 * <p>
 * Follows the Singleton pattern (a supporting pattern; the project's primary declared
 * pattern is the Factory used to create ships).
 *
 */
public final class ViewManager {

    private static final String WINDOW_TITLE = "Batalla Naval - Universidad del Valle";

    private static final String START_VIEW_PATH =
            "/com/example/batallanaval/view/start-view.fxml";
    private static final String GAME_VIEW_PATH =
            "/com/example/batallanaval/view/game-view.fxml";
    private static final String PLACEMENT_VIEW_PATH =
            "/com/example/batallanaval/view/placement-view.fxml";

    private static final ViewManager INSTANCE = new ViewManager();

    private Stage primaryStage;

    private ViewManager() {
    }

    /**
     * @return the shared {@code ViewManager} instance
     */
    public static ViewManager getInstance() {
        return INSTANCE;
    }

    /**
     * Stores the primary stage and applies the base window configuration.
     *
     * @param stage the primary stage provided by JavaFX
     */
    public void initialize(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle(WINDOW_TITLE);
        this.primaryStage.setResizable(false);
    }

    /**
     * Loads and displays the start view.
     */
    public void showStartView() {
        swapScene(START_VIEW_PATH);
    }

    /**
     * Loads and displays the manual placement view, running the given initialization
     * action on its controller (for example, to pass the player's nickname).
     *
     * @param controllerInit action to run on the loaded controller before showing it
     * @param <T>            the controller type of the placement view
     */
    public <T> void showPlacementView(Consumer<T> controllerInit) {
        swapScene(PLACEMENT_VIEW_PATH, controllerInit);
    }

    /**
     * Loads and displays the game view, running the given initialization action on its
     * controller (start a new game or resume a saved one).
     *
     * @param controllerInit action to run on the loaded controller before showing it
     * @param <T>            the controller type of the game view
     */
    public <T> void showGameView(Consumer<T> controllerInit) {
        swapScene(GAME_VIEW_PATH, controllerInit);
    }

    /**
     * Loads the FXML at the given path with no controller initialization.
     */
    private void swapScene(String fxmlPath) {
        swapScene(fxmlPath, null);
    }

    /**
     * Loads the FXML at the given path, optionally runs an initialization action on
     * its controller, and sets it as the current scene.
     *
     * @param fxmlPath       the absolute classpath path of the FXML to load
     * @param controllerInit optional action to run on the loaded controller; may be null
     * @param <T>            the controller type
     * @throws IllegalStateException if the FXML cannot be located or loaded
     */
    private <T> void swapScene(String fxmlPath, Consumer<T> controllerInit) {
        URL resourceUrl = ViewManager.class.getResource(fxmlPath);
        if (resourceUrl == null) {
            throw new IllegalStateException(
                    "No se pudo encontrar el archivo FXML en: " + fxmlPath);
        }

        try {
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            if (controllerInit != null) {
                T controller = loader.getController();
                controllerInit.accept(controller);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo cargar la vista: " + fxmlPath, e);
        }
    }
}