package cellsociety;

import cellsociety.controller.GameManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application entry point that launches the Cellsociety UI.
 *
 * <p>
 * Purpose: Bootstraps the JavaFX runtime and constructs a GameManager for the
 * primary
 * stage.
 * </p>
 *
 * <p>
 * Assumptions: JavaFX environment is available. main(...) should be invoked to
 * start the JavaFX
 * platform.
 * </p>
 *
 * <p>
 * Dependencies: cellsociety.controller.GameManager and JavaFX Application APIs.
 * </p>
 *
 * @author Georgiy Zemlevskiy
 */
public class Main extends Application {

  /**
   * Launches the JavaFX application.
   *
   * @param args command line arguments passed to the application
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * JavaFX entry point that initializes the GameManager on the provided stage.
   *
   * @param primaryStage the primary JavaFX stage
   */
  @Override
  public void start(Stage primaryStage) {
    new GameManager(primaryStage);
  }
}
