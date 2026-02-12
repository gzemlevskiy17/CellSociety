package cellsociety;

import cellsociety.controller.GameEngine;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    new GameEngine(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
