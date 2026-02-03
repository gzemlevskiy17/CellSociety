package cellsociety.view;

import cellsociety.view.GridView;
import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

    private Stage stageLive;
    private GridView gridView;

    public ScreenManager(Stage stage, GridView gridToUse) {
        stageLive = stage;
        gridView = gridToUse;
    }

    public void showGridView() {
        gridView.start(stageLive);
    }
}
