package cellsociety.view.grids;

import java.util.Map;

import cellsociety.controller.Controller;
import cellsociety.controller.GameRunner;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * Abstract base class for rendering a cellular automaton grid in JavaFX.
 *
 * <p>Manages grid dimensions, cell sizing within a fixed display area, and the
 * color map used to paint each cell state. Subclasses implement
 * {@link #initializeGridPane()}, {@link #updateCell(int, int, int)}, and
 * {@link #addMouseEvents(cellsociety.controller.Controller)} for their specific shape.</p>
 *
 * @author cellsociety team
 */
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

    /**
     * Returns the root JavaFX pane containing the rendered grid.
     *
     * @return the grid's root {@link javafx.scene.layout.Pane}
     */
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

    protected void addToShape(Shape cell) {
        cell.setFill(getGridColors().getOrDefault(0, Color.WHITE));
        cell.setStroke(Color.BLACK);
        cell.setStrokeWidth(1);
        cell.setStrokeType(StrokeType.INSIDE);
    }

    /**
     * Returns the display name of the simulation, formatted with spaces before
     * each capital letter (e.g., "GameOfLife" becomes "Game Of Life").
     *
     * @return the human-readable game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Returns the current state-to-color mapping used to paint cells.
     *
     * @return a map from state integer to {@link javafx.scene.paint.Color}
     */
    public Map<Integer, Color> retAllColor() {
        return gridColors;
    }

    /**
     * Replaces the state-to-color mapping with the given values, updating all
     * entries that exist in {@code newValue}.
     *
     * @param newValue the new state-to-color entries to apply
     */
    public void setAllColors(Map<Integer, Color> newValue) {
        gridColors.putAll(newValue);
    }

    /**
     * Returns the number of rows in this grid view.
     *
     * @return the row count
     */
    public int getNumRows() {
        return gridRowNum;
    }

    /**
     * Returns the number of columns in this grid view.
     *
     * @return the column count
     */
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
