package cellsociety.view.grids;

import java.util.Map;

import cellsociety.controller.Controller;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Renders the simulation grid as a uniform rectangular grid of
 * {@link javafx.scene.shape.Rectangle} cells.
 *
 * @author cellsociety team
 */
public class RectangleGridView extends GridView {

    private Rectangle[][] rectArray;
    private GridPane gridPane;

    public RectangleGridView(String gameName, Map<Integer, Color> colors, int x, int y) {
        super(gameName, colors, x, y);
    }

    @Override
    public void initializeGridPane() {
        gridPane = (GridPane) getGridPane();
        rectArray = new Rectangle[getNumRows()][getNumCols()];

        double y = 0;
        for (int i = 0; i < getNumRows(); i++) {
            double x = 0;
            for (int j = 0; j < getNumCols(); j++) {
                initializeGridRectangles(i, j, x, y);
                x += getWidth();
            }
            y += getHeight();
        }
    }

    private void initializeGridRectangles(int row, int col, double x, double y) {

        Rectangle cellRect = new Rectangle(getWidth(), getHeight());
        cellRect.setLayoutX(x);
        cellRect.setLayoutY(y);
        addToShape(cellRect);

        rectArray[row][col] = cellRect;
        gridPane.add(cellRect, col, row);
    }

    @Override
    public void updateCell(int row, int col, int state) {
        rectArray[row][col].setFill(getGridColors().get(state));
    }

    @Override
    public void addMouseEvents(Controller controller) {
        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {
                int indepRow = row;
                int indepCol = col;

                rectArray[row][col].setOnMouseClicked(event -> {
                    controller.pushFromScreentoGrid(indepRow, indepCol);
                });
            }
        }
    }

}
