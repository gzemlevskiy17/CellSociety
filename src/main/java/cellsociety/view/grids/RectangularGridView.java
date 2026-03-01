package cellsociety.view.grids;

import cellsociety.view.GridView;
import java.util.HashMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangularGridView extends GridView {

    private Rectangle[][] RectArray;

    public RectangularGridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
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
        int state = 0;

        Rectangle cellRect = new Rectangle(getWidth(), getHeight());
        cellRect.setLayoutX(x);
        cellRect.setLayoutY(y);
        cellRect.setFill(getGridColors().get(state));
        cellRect.setStroke(Color.BLACK);
        cellRect.setStrokeWidth(2);

        RectArray[row][col] = cellRect;
        ((javafx.scene.layout.GridPane) getGridPane()).add(cellRect, col, row);
    }

    @Override
    public void updateCell(int row, int col, int state) {
        RectArray[row][col].setFill(getGridColors().get(state));
    }
}
