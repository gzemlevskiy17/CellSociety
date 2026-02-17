package cellsociety.view;

import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public abstract class GridView {

    protected HashMap<Integer, Color> gridColors = new HashMap<Integer, Color>();
    protected String gameName;

    protected Scene mainScene;
    protected GridPane mainPane;
    protected BorderPane bottomPane;
    protected int gridRowNum;
    protected int gridColNum;
    protected double MAX_GRID_WIDTH = 800.0;
    protected double MAX_GRID_HEIGHT = 500.0;
    protected double width;
    protected double height;

    // going to assume the Game will include the colors
    public GridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
        this.gameName = gameName.replaceAll("([a-z])([A-Z])", "$1 $2");
        mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setHgap(2);
        mainPane.setVgap(2);

        gridRowNum = y;
        gridColNum = x;
        width = MAX_GRID_WIDTH / gridColNum;
        height = MAX_GRID_HEIGHT / gridRowNum;

        gridColors = colors;
        initializeGridPane();
    }

    protected abstract void initializeGridPane();

    public abstract void updateCell(int row, int col, int state);

    public GridPane getGridPane() {
        return mainPane;
    }

    public int getNumRows() {
        return gridRowNum;
    }

    public int getNumCols() {
        return gridColNum;
    }
}
