package cellsociety.view;

import java.util.HashMap;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Renders the simulation grid as a triangular lattice.
 *
 * <p>
 * Triangles alternate between pointing up (▲) and pointing down (▽) based on
 * the parity of {@code (row + col)}. Each triangle fills the same rectangular
 * bounding box, so all columns are the same width and all rows the same height,
 * creating a seamless tiling.
 * </p>
 *
 * @author Michael Li
 */
public class TriangularGridView extends GridView {

  private static final double MAX_GRID_WIDTH = 800.0;
  private static final double MAX_GRID_HEIGHT = 500.0;

  private Polygon[][] triArray;

  public TriangularGridView(String gameName, HashMap<Integer, Color> colors, int cols, int rows) {
    super(gameName, colors, cols, rows);
  }

  @Override
  public void initializeGridPane() {
    Pane pane = new Pane();
    pane.setPrefSize(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
    setMainPane(pane);

    int rows = getNumRows();
    int cols = getNumCols();
    
    // Triangle width: each column is half a triangle base
    // So we need cols/2 full triangles across
    double triWidth = MAX_GRID_WIDTH / (cols / 2.0);
    // Height of equilateral triangle: sqrt(3)/2 * base
    double triHeight = triWidth * Math.sqrt(3) / 2.0;
    // Total rows needed vertically
    double totalHeight = rows * triHeight;
    double scale = Math.min(1.0, MAX_GRID_HEIGHT / totalHeight);
    triWidth *= scale;
    triHeight *= scale;

    triArray = new Polygon[rows][cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        createTriangle(row, col, triWidth, triHeight, pane);
      }
    }
  }

  private void createTriangle(int row, int col, double triWidth, double triHeight, Pane pane) {
    // Each cell is half the width of a triangle base
    double halfWidth = triWidth / 2.0;
    double x = col * halfWidth;
    double y = row * triHeight;

    Polygon tri;
    if ((row + col) % 2 == 0) {
      // Points up ▲
      tri = new Polygon(
          x, y + triHeight,           // bottom-left
          x + triWidth, y + triHeight, // bottom-right
          x + halfWidth, y             // top
      );
    } else {
      // Points down ▽
      tri = new Polygon(
          x, y,                        // top-left
          x + triWidth, y,             // top-right
          x + halfWidth, y + triHeight // bottom
      );
    }

    tri.setFill(getGridColors().getOrDefault(0, Color.WHITE));
    tri.setStroke(Color.BLACK);
    tri.setStrokeWidth(1);

    triArray[row][col] = tri;
    pane.getChildren().add(tri);
  }

  @Override
  public void updateCell(int row, int col, int state) {
    triArray[row][col].setFill(getGridColors().getOrDefault(state, Color.WHITE));
  }
}
