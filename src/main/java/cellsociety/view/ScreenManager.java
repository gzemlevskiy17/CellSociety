package cellsociety.view;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

  private Stage stageLive;
  private GridView gridView;

  public ScreenManager(Stage stage, GridView gridToUse) {
    stageLive = stage;
    gridView = gridToUse;
  }

  public void gameScreen() throws Exception {
    Scene scene = new Scene(gridView.getGridPane(), 400, 400);
    stageLive.setScene(scene);
//  gridView.start(stageLive);
  }

  public void startScreen() {

  }

  public void editSaveScreen() {

  }

  public void endScreen() {

  }
}
