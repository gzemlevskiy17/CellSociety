package cellsociety.view.grids;

import java.util.Map;

import cellsociety.controller.Controller;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Renders the simulation grid as a uniform rectangular grid of
 * {@link javafx.scene.shape.Rectangle} cells.
 *
 * @author cellsociety team
 */
public class RectangleGridView extends GridView {

    private Rectangle[][] RectArray;

    public RectangleGridView(String gameName, Map<Integer, Color> colors, int x, int y) {
        super(gameName, colors, x, y);
    }

    @Override
    public void initializeGridPane() {
        RectArray = new Rectangle[getNumRows()][getNumCols()];

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

        RectArray[row][col] = cellRect;
        ((javafx.scene.layout.GridPane) getGridPane()).add(cellRect, col, row);
    }

    @Override
    public void updateCell(int row, int col, int state) {
        RectArray[row][col].setFill(getGridColors().get(state));
    }

    @Override
    public void addMouseEvents(Controller controller) {
        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {
                int indepRow = row;
                int indepCol = col;

                RectArray[row][col].setOnMouseClicked(event -> {
                    controller.pushFromScreentoGrid(indepRow, indepCol);
                });
            }
        }
    }

    @Override
    public void toggleGridOutline() {
        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {
                if (RectArray[row][col].getStrokeWidth() > 0) {
                    RectArray[row][col].setStrokeWidth(0);
                } else {
                    RectArray[row][col].setStrokeWidth(1);
                }
            }
        }
    }
}
