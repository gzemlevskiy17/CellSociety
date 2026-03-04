package cellsociety.view;

import cellsociety.controller.GameRunner;
import cellsociety.model.ParseResult;
import cellsociety.view.grids.GridView;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory that creates {@link GridView} instances from parsed XML configuration
 * data.
 *
 * <p>
 * Converts hex color strings from the {@link ParseResult} into JavaFX
 * {@link Color} objects and builds a {@link GridView}. Falls back to default
 * colors (white, black, red) if no color map is provided in the XML.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link ParseResult}, {@link GridView}
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
 * @author Georgiy Zemlevskiy
 */
public class ViewFactory {

  private static final Logger LOG = LogManager.getLogger(ViewFactory.class);

  /**
   * Creates a {@link GridView} with colors from the parsed configuration. Falls
   * back to default colors (white, black, red) if no color map is provided.
   *
   * @param result the parsed XML configuration
   * @return a new {@link GridView} configured for this simulation
   */
  public GridView createView(ParseResult result) {
    int rows = result.getRows();
    int cols = result.getCols();
    String gameType = result.getGameType();

    Map<Integer, Color> colorMap = new HashMap<>();
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

    try {
      Class<?>[] gameParamTypes = new Class<?>[]{String.class, Map.class, int.class, int.class};
      return (GridView) Class.forName(
              "cellsociety.view.grids." + shape + "GridView")
          .getDeclaredConstructor(gameParamTypes)
          .newInstance(gameType, colorMap, cols, rows);
    } catch (Exception e) {
      String message = shape + " constructor could not be invoked";
      LOG.warn(message, e);
      throw new IllegalArgumentException(message, e);
    }
  }
}
