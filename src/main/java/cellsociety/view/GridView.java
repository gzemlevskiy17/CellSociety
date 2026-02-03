package cellsociety.view;

import java.util.HashMap;
import cellsociety.model.Game;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public abstract class GridView extends Application {

    private HashMap<Integer, Color> gridColors = new HashMap<Integer, Color>();
    private Game gameStored;
    private int cellSize = 30;

    // going to assume the Game will include the colors
    public GridView(Game gameToDisplay) {
        gameStored = gameToDisplay;
    }

    public abstract Scene render();

    @Override
    public abstract void start(Stage stage) throws Exception;
}
