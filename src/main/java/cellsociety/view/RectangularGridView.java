package cellsociety.view;

import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RectangularGridView extends GridView {

    public RectangularGridView(Game gameToDisplay) {
        super(gameToDisplay);
    }

    @Override
    public Scene render() {
        return new Scene(grid);
    }

    @Override
    public void initializeGridPane() {
        // Set up the rows and columns.
        for (int i = 0; i < gridSize; i++) {
            mainPane.getColumnConstraints().add(new ColumnConstraints(cellSize));
            mainPane.getRowConstraints().add(new RowConstraints(cellSize));
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                initializeGridRectangles(i, j);
            }
        }
    }

    private void initializeGridRectangles(int row, int col) {

        // All cells will initially be dead, build a Rectangle for each.
        Rectangle cellRect = new Rectangle();
        cellRect.setHeight(cellSize);
        cellRect.setWidth(cellSize);
        cellRect.setFill(Color.WHITE);

        // Add the Rectangle to the grid at the col/row.
        mainPane.add(cellRect, col, row);
    }
}
