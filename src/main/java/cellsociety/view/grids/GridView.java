package cellsociety.view.grids;

import java.util.Map;

import cellsociety.controller.Controller;
import cellsociety.controller.GameRunner;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class GridView {

    private static final double MAX_GRID_WIDTH = 800.0; // TODO: put in property files & handle duplicates, super?
    private String gameName;
    private Map<Integer, Color> gridColors;
    private static final double MAX_GRID_HEIGHT = 500.0;

    private int gridRowNum;
    private int gridColNum;
    private double width;
    private double height;

    private Pane mainPane;

    public GridView(String gameName, Map<Integer, Color> colors, int x, int y) {
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

    public abstract void addMouseEvents(Controller controller);

    public abstract void toggleGridOutline();

    public Pane getGridPane() {
        return mainPane;
    }

    protected void setMainPane(Pane pane) {
        mainPane = pane;
    }

    protected double getMaxGridWidth() {
        return MAX_GRID_WIDTH;
    }

    protected double getMaxGridHeight() {
        return MAX_GRID_HEIGHT;
    }

    protected Pane createDisplayPane() {
        Pane pane = new Pane();
        pane.setPrefSize(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
        setMainPane(pane);
        return pane;
    }

    public String getGameName() {
        return gameName;
    }

    public Map<Integer, Color> retAllColor() {
        return gridColors;
    }

    public void setAllColors(Map<Integer, Color> newValue) {
        gridColors.putAll(newValue);
    }

    public int getNumRows() {
        return gridRowNum;
    }

    public int getNumCols() {
        return gridColNum;
    }

    protected Map<Integer, Color> getGridColors() {
        return gridColors;
    }

    protected double getWidth() {
        return width;
    }

    protected double getHeight() {
        return height;
    }
}
