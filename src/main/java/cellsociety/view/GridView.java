package cellsociety.view;

import java.util.HashMap;
import cellsociety.model.Game;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    protected double MAX_GRID_WIDTH = 350.0;
    protected double MAX_GRID_HEIGHT = 350.0;
    protected double width;
    protected double height;

    // going to assume the Game will include the colors
    public GridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
        this.gameName = gameName;
        mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setHgap(2);
        mainPane.setVgap(2);

        gridRowNum = y;
        gridColNum = x;
        width = MAX_GRID_WIDTH/gridColNum;
        height = MAX_GRID_HEIGHT/gridRowNum;

        gridColors = colors;
        initializeGridPane();
        initializeBorderPane();
        initializeScene();
    }

    protected abstract void initializeGridPane();

    protected abstract void initializeBorderPane();

    protected abstract void initializeScene();

    public abstract Scene render();

    public abstract void updateGrid(Map<Point, Integer> updateMap);
}
