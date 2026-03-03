package cellsociety.view;

import java.util.HashMap;
import java.util.Map;

import cellsociety.model.ParseResult;
import cellsociety.view.grids.GridView;
import cellsociety.view.grids.HexagonalGridView;
import cellsociety.view.grids.RectangularGridView;
import cellsociety.view.grids.TriangularGridView;
import javafx.scene.paint.Color;

/**
 * Factory that creates {@link GridView} instances from parsed XML configuration
 * data.
 *
 * <p>
 * Converts hex color strings from the {@link ParseResult} into JavaFX
 * {@link Color}
 * objects and builds a {@link RectangularGridView}. Falls back to default
 * colors
 * (white, black, red) if no color map is provided in the XML.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link ParseResult}, {@link RectangularGridView}
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * ParseResult result = XMLReader.parse(new File("data/GameOfLife/GameOfLifeSimple.xml"));
 * ViewFactory factory = new ViewFactory();
 * GridView view = factory.createView(result);
 * }</pre>
 *
 * @author Michael Li
 */
public class ViewFactory {

  /**
   * Creates a {@link GridView} with colors from the parsed configuration.
   * Falls back to default colors (white, black, red) if no color map is provided.
   *
   * @param result the parsed XML configuration
   * @return a new {@link RectangularGridView} configured for this simulation
   */
  public GridView createView(ParseResult result) {
    int rows = result.getRows();
    int cols = result.getCols();
    String gameType = result.getGameType();

    HashMap<Integer, Color> colorMap = new HashMap<>();
    Map<Integer, String> parsedColors = result.getColorMap();

    if (parsedColors != null && !parsedColors.isEmpty()) {
      for (Integer id : parsedColors.keySet()) {
        colorMap.put(id, Color.web(parsedColors.get(id)));
      }
    } else {
      colorMap.put(0, Color.WHITE);
      colorMap.put(1, Color.BLACK);
      colorMap.put(2, Color.RED);
    }
    String shape = result.getShape();
    return switch (shape) {
      case "Hexagonal" -> new HexagonalGridView(gameType, colorMap, cols, rows);
      case "Triangular" -> new TriangularGridView(gameType, colorMap, cols, rows);
      default -> new RectangularGridView(gameType, colorMap, cols, rows);
    };
  }
}
