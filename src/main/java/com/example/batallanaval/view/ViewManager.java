package com.example.batallanaval.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Centralized manager responsible for loading FXML views and swapping
 * scenes on the primary stage.
 * <p>
 * This class is the single point of contact with the JavaFX {@link Stage}.
 * No controller, model or other view touches the stage directly; instead,
 * they request screen changes through this manager. This keeps navigation
 * logic in one place and guarantees the stage is always loaded consistently.
 * </p>
 *
 * <p>The class follows the Singleton pattern. Note: for the design-pattern
 * rubric requirement, the project's primary declared pattern is the Factory
 * Method (used to create ships); this Singleton is a supporting pattern.</p>
 *
 */
public final class ViewManager {

    /** Title shown in the application window. */
    private static final String WINDOW_TITLE = "Batalla Naval - Universidad del Valle";

    /** Classpath location of the start view. */
    private static final String START_VIEW_PATH =
            "/com/example/batallanaval/view/start-view.fxml";

    /** Classpath location of the game view. */
    private static final String GAME_VIEW_PATH =
            "/com/example/batallanaval/view/game-view.fxml";

    /** The single shared instance of this manager. */
    private static final ViewManager INSTANCE = new ViewManager();

    /** The primary stage provided by the JavaFX runtime. */
    private Stage primaryStage;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ViewManager() {
    }

    /**
     * Returns the single shared instance of the view manager.
     *
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
     * Loads and displays the start view, where the player enters a nickname
     * and chooses to start a new game or continue a saved one.
     */
    public void showStartView() {
        swapScene(START_VIEW_PATH);
    }

    /**
     * Loads and displays the game view, running the given initialization action on its
     * controller (for example, to start a new game or resume a saved one).
     *
     * @param controllerInit action to run on the loaded controller before showing it
     * @param <T>            the controller type of the game view
     */
    public <T> void showGameView(Consumer<T> controllerInit) {
        swapScene(GAME_VIEW_PATH, controllerInit);
    }

    /**
     * Loads the FXML at the given classpath path and sets it as the current
     * scene, with no controller initialization.
     *
     * @param fxmlPath the absolute classpath path of the FXML to load
     * @throws IllegalStateException if the FXML cannot be located or loaded
     */
    private void swapScene(String fxmlPath) {
        swapScene(fxmlPath, null);
    }

    /**
     * Loads the FXML at the given classpath path, optionally runs an initialization
     * action on its controller, and sets it as the current scene. Fails fast with a
     * descriptive message if the resource is missing.
     *
     * @param fxmlPath       the absolute classpath path of the FXML to load
     * @param controllerInit optional action to run on the loaded controller; may be
     *                       {@code null}
     * @param <T>            the controller type
     * @throws IllegalStateException if the FXML cannot be located or loaded
     */
    private <T> void swapScene(String fxmlPath, Consumer<T> controllerInit) {
        URL resourceUrl = ViewManager.class.getResource(fxmlPath);
        if (resourceUrl == null) {
            throw new IllegalStateException(
                    "No se pudo encontrar el archivo FXML en: " + fxmlPath
            );
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
                    "No se pudo cargar la vista: " + fxmlPath, e
            );
        }
    }
}