package cellsociety.view.grids;

import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class GridView {

    private HashMap<Integer, Color> gridColors;
    private String gameName;

    private static final double MAX_GRID_WIDTH = 800.0;
    private static final double MAX_GRID_HEIGHT = 500.0;

    private int gridRowNum;
    private int gridColNum;
    private double width;
    private double height;

    private Pane mainPane;

    public GridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
        this.gameName = gameName.replaceAll("([a-z])([A-Z])", "$1 $2");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        mainPane = gridPane;

        gridRowNum = y;
        gridColNum = x;
        width = MAX_GRID_WIDTH / gridColNum;
        height = MAX_GRID_HEIGHT / gridRowNum;

        gridColors = colors;
        initializeGridPane();
    }

    protected abstract void initializeGridPane();

    public abstract void updateCell(int row, int col, int state);

    public Pane getGridPane() {
        return mainPane;
    }

    protected void setMainPane(Pane pane) {
        mainPane = pane;
    }

    public String getGameName() {
        return gameName;
    }

    public HashMap<Integer, Color> retAllColor() {
        return gridColors;
    }

    public void setAllColors(HashMap<Integer, Color> newValue) {
        gridColors.putAll(newValue);
    }

    public int getNumRows() {
        return gridRowNum;
    }

    public int getNumCols() {
        return gridColNum;
    }

    protected HashMap<Integer, Color> getGridColors() {
        return gridColors;
    }

    protected double getWidth() {
        return width;
    }

    protected double getHeight() {
        return height;
    }
}
