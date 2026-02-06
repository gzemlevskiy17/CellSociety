package cellsociety.view;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

  private Stage stageLive;
  private GridView gridView;
  private String gameName;
  private String author;
  private String description;

  public ScreenManager(Stage stage, GridView gridToUse, String gameName, String author) {
    stageLive = stage;
    gridView = gridToUse;
    gameName = gameName;
    author = author;
    description = description;
    gameScreen();
    stage.show();
  }

  public void gameScreen() {
    Scene scene = new Scene(gridView.getGridPane(), 400, 400);
    stageLive.setScene(scene);
  }

  public void startScreen() {

  }

  public void editSaveScreen() {

  }

  public void endScreen() {

  }
}
