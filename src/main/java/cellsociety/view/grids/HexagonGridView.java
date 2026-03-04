package cellsociety.view.grids;

import java.util.Map;

import cellsociety.controller.Controller;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

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
public class HexagonGridView extends GridView {

  private Polygon[][] hexArray;
  private double circumRadius;

  public HexagonGridView(String gameName, Map<Integer, Color> colors, int cols, int rows) {
    super(gameName, colors, cols, rows);
  }

  @Override
  public void initializeGridPane() {
    // Replace the default GridPane with a free-layout Pane
    Pane pane = createDisplayPane();

    int rows = getNumRows();
    int cols = getNumCols();

    // Pointy-top hex: height = 2r, width = sqrt(3)*r
    // Vertical spacing between centers: 1.5 * r
    // Horizontal spacing between centers: sqrt(3) * r
    double rFromHeight = getMaxGridHeight() / (rows * 1.5 + 0.5);
    double rFromWidth = getMaxGridWidth() / (cols * Math.sqrt(3) + Math.sqrt(3) / 2.0);
    circumRadius = Math.min(rFromHeight, rFromWidth);

    hexArray = new Polygon[rows][cols];

    // Calculate total grid dimensions for centering
    double hexW = Math.sqrt(3) * circumRadius;
    double totalWidth = hexW * cols + hexW / 2.0;
    double totalHeight = circumRadius * (1.5 * rows + 0.5);
    double offsetX = (getMaxGridWidth() - totalWidth) / 2.0;
    double offsetY = (getMaxGridHeight() - totalHeight) / 2.0;

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
    addToShape(hex);

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

  @Override
  public void addMouseEvents(Controller controller) {
    for (int row = 0; row < getNumRows(); row++) {
      for (int col = 0; col < getNumCols(); col++) {
        int indepRow = row;
        int indepCol = col;
        hexArray[row][col].setOnMouseClicked(event -> {
          controller.pushFromScreentoGrid(indepRow, indepCol);
        });
      }
    }
  }

  @Override
  public void toggleGridOutline() {
    for (int row = 0; row < getNumRows(); row++) {
      for (int col = 0; col < getNumCols(); col++) {
        if (hexArray[row][col].getStrokeWidth() > 0) {
          hexArray[row][col].setStrokeWidth(0);
        } else {
          hexArray[row][col].setStrokeWidth(1);
        }
      }
    }
  }
}
