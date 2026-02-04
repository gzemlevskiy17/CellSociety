package cellsociety.view;

import java.util.HashMap;
import cellsociety.model.Game;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public abstract class GridView {

    protected HashMap<Integer, Color> gridColors = new HashMap<Integer, Color>();
    protected String gameName;

    protected Scene mainScene;
    protected GridPane mainPane;
    protected BorderPane bottomPane;
    protected int gridRowNum;
    protected int gridColNum;

    // going to assume the Game will include the colors
    public GridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
        this.gameName = gameName;
        gridRowNum = y;
        gridColNum = x;
        gridColors = colors;
        initializeGridPane();
        initializeBorderPane();
        initializeScene();
    }

    protected abstract void initializeGridPane();

    protected abstract void initializeBorderPane();

    protected abstract void initializeScene();

    public abstract Scene render();

    public Scene getMainScene() {
        return mainScene;
    }
}
