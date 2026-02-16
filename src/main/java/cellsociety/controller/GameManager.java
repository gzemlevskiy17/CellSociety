package cellsociety.controller;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameManager {
  private Stage primaryStage;

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
  }


}
