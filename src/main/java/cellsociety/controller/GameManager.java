package cellsociety.controller;

import cellsociety.GameFactory;
import cellsociety.ParseResult;
import cellsociety.model.Game;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import javafx.stage.Stage;

public class GameManager {
  private Stage primaryStage;

  public GameManager(Stage primaryStage) {
    this.primaryStage = primaryStage;
    launchNewGame(primaryStage);
  }

  private void createNewGame() {
    launchNewGame(createNewStage());
  }

  private Stage createNewStage() {
    return new Stage();
  }

  //TODO: implement new window location/window tiling (needs to be documented)
  private void launchNewGame(Stage newStage) {
    GameRunner gameRunner = new GameRunner(newStage, this::createNewGame);
  }


}
