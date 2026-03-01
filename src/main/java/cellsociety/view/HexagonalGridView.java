package cellsociety.view;

import java.util.HashMap;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Renders the simulation grid as a pointy-top hexagonal lattice.
 *
 * <p>
 * Each cell is drawn as a regular hexagon. Odd rows are offset horizontally
 * by half a hex width to create the classic honeycomb layout. The hexagon size
 * is calculated to fill the available display area (800 x 500) as tightly as
 * possible without clipping.
 * </p>
 *
 * @author Michael Li
 */
public class HexagonalGridView extends GridView {

  private static final double MAX_GRID_WIDTH = 800.0;
  private static final double MAX_GRID_HEIGHT = 500.0;

  private Polygon[][] hexArray;
  private double circumRadius;

  public HexagonalGridView(String gameName, HashMap<Integer, Color> colors, int cols, int rows) {
    super(gameName, colors, cols, rows);
  }

  @Override
  public void initializeGridPane() {
    // Replace the default GridPane with a free-layout Pane
    Pane pane = new Pane();
    pane.setPrefSize(MAX_GRID_WIDTH, MAX_GRID_HEIGHT);
    setMainPane(pane);

    int rows = getNumRows();
    int cols = getNumCols();

    // Pointy-top hex: height = 2r, width = sqrt(3)*r
    // Vertical spacing between centers: 1.5 * r
    // Horizontal spacing between centers: sqrt(3) * r
    double rFromHeight = MAX_GRID_HEIGHT / (rows * 1.5 + 0.5);
    double rFromWidth = MAX_GRID_WIDTH / (cols * Math.sqrt(3) + Math.sqrt(3) / 2.0);
    circumRadius = Math.min(rFromHeight, rFromWidth);

    hexArray = new Polygon[rows][cols];
    
    // Calculate total grid dimensions for centering
    double hexW = Math.sqrt(3) * circumRadius;
    double totalWidth = hexW * cols + hexW / 2.0;
    double totalHeight = circumRadius * (1.5 * rows + 0.5);
    double offsetX = (MAX_GRID_WIDTH - totalWidth) / 2.0;
    double offsetY = (MAX_GRID_HEIGHT - totalHeight) / 2.0;
    
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        createHex(row, col, pane, offsetX, offsetY);
      }
    }
  }

  private void createHex(int row, int col, Pane pane, double offsetX, double offsetY) {
    double hexW = Math.sqrt(3) * circumRadius;
    double vSpacing = circumRadius * 1.5;

    double cx = offsetX + hexW / 2.0 + col * hexW + (row % 2 == 1 ? hexW / 2.0 : 0);
    double cy = offsetY + circumRadius + row * vSpacing;

    Polygon hex = new Polygon(buildHexPoints(cx, cy));
    hex.setFill(getGridColors().getOrDefault(0, Color.WHITE));
    hex.setStroke(Color.BLACK);
    hex.setStrokeWidth(1);

    hexArray[row][col] = hex;
    pane.getChildren().add(hex);
  }

  /**
   * Returns the 6 vertex coordinates for a pointy-top hexagon centered at (cx,
   * cy).
   */
  private double[] buildHexPoints(double cx, double cy) {
    double r = circumRadius;
    double half = r / 2.0;
    double side = Math.sqrt(3) * r / 2.0;
    return new double[] {
        cx, cy - r,
        cx + side, cy - half,
        cx + side, cy + half,
        cx, cy + r,
        cx - side, cy + half,
        cx - side, cy - half
    };
  }

  @Override
  public void updateCell(int row, int col, int state) {
    hexArray[row][col].setFill(getGridColors().getOrDefault(state, Color.WHITE));
  }
}
