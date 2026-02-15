package cellsociety.view;

import cellsociety.ParseResult;
import java.util.HashMap;
import javafx.scene.paint.Color;

public class ViewFactory {

  public GridView createView(ParseResult result) {
    int rows = result.getRows();
    int cols = result.getCols();
    String gameType = result.getGameType();

    HashMap<Integer, Color> colorMap = new HashMap<>();
    java.util.Map<Integer, String> parsedColors = result.getColorMap();

    if (parsedColors != null && !parsedColors.isEmpty()) {
      for (Integer id : parsedColors.keySet()) {
        colorMap.put(id, Color.web(parsedColors.get(id)));
      }
    } else {
      colorMap.put(0, Color.WHITE);
      colorMap.put(1, Color.BLACK);
      colorMap.put(2, Color.RED);
    }

    return new RectangularGridView(gameType, colorMap, cols, rows);
  }
}
