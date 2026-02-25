package cellsociety.controller;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Top-level manager that launches GameRunner instances and supports creating
 * new windows.
 *
 * <p>
 * Purpose: Provide a simple entry point to open the initial game window and
 * create
 * additional windows centered on screen. Useful as the application's bootstrap
 * class that ties
 * JavaFX Stage lifecycle to GameRunner creation.
 * </p>
 *
 * <p>
 * Assumptions: JavaFX is initialized and primaryStage is non-null. New stages
 * are created
 * with default sizing; callers may need to adjust layout if custom sizing is
 * required.
 * </p>
 *
 * <p>
 * Dependencies: GameRunner and JavaFX Stage/Screen APIs.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * new GameManager(primaryStage);
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 */
public class GameManager {

  private Stage primaryStage;

  /**
   * Initializes the GameManager which immediately launches a new game window
   * using the provided
   * primary stage.
   *
   * @param primaryStage the JavaFX stage to host the first game instance
   */
  public GameManager(Stage primaryStage) {
    this.primaryStage = primaryStage;
    launchNewGame(primaryStage);
  }

  private void createNewGame() {
    Stage newStage = createNewStage();
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    newStage.setX((screenBounds.getWidth() - newStage.getWidth()) / 2);
    newStage.setY((screenBounds.getHeight() - newStage.getHeight()) / 2);
    launchNewGame(newStage);
  }

  private Stage createNewStage() {
    return new Stage();
  }

  private void launchNewGame(Stage newStage) {
    GameRunner gameRunner = new GameRunner(newStage, this::createNewGame);
    // TODO: consider if we need to keep track of game runners for any reason.
    // TODO: add launch method to GameRunner to make this code clearer
  }

}
