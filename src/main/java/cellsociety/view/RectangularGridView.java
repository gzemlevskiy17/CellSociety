package cellsociety.view;

import java.util.HashMap;

import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RectangularGridView extends GridView {

    private Rectangle[][] RectArray;

    public RectangularGridView(String gameName, HashMap<Integer, Color> colors, int x, int y) {
        super(gameName, colors, x, y);
    }

    @Override
    public Scene render() {
        return new Scene(grid);
    }

    @Override
    public void initializeGridPane() {
        RectArray = newRectangle[gridRowNum][gridColNum]

        double  y = 0;
        for (int i = 0; i < gridSize; i++) {
            double x = 0;
            for (int j = 0; j < gridSize; j++) {
                initializeGridRectangles(i, j, x, y);
                x += width;
            }
            y += height;
        }
    }

    private void initializeGridRectangles(int row, int col, double x, double y) {
        // All cells will initially be dead, build a Rectangle for each.
        int state = 0;

        Rectangle cellRect = new Rectangle(width, height);
        cellRect.setLayoutX(x);
        cellRect.setLayoutY(y);
        cellRect.setFill(gridColors.get(Integer.valueOf(state)));
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);

        // Add the Rectangle to the grid at the col/row.
        RectArray[row][col] = cellRect;
        mainPane.add(cellRect, col, row);
    }

    public void updateGrid(Map<Point, Integer> updateMap) {
        for(Point p: updatedMap.keySet()){
            grid[p.getY()][p.getX()].setFill(gridColors.get(updatedMap.get(p)));
        }
    }

    @Override
    public void initializeBorderPane() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeScene() {
        mainPane = newScene(mainPane, 400, 400);
    }
}
