package cellsociety.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Validates parsed XML data for simulation configuration files.
 *
 * <p>
 * Separates validation concerns from XML parsing. All methods throw
 * {@link IllegalArgumentException} on invalid input.
 * </p>
 *
 * @author Michael Li
 */
class XMLValidator {

  static final List<String> VALID_TYPES = Arrays.asList(
      "GameOfLife", "ForestFire", "Segregation", "Percolation", "WaTor", "FallingSand",
      "ForagingAnts", "SugarScape", "LangtonsLoop", "SDSRLoop");

  static final List<String> VALID_NEIGHBORHOODS = Arrays.asList(
      "Moore", "VonNeumann", "ExtendedMoore", "Hexagonal", "Triangular");

  static final List<String> VALID_EDGES = Arrays.asList(
      "Bounded", "Toroidal", "Mirror");

  static final List<String> VALID_SHAPES = Arrays.asList(
      "Rectangle", "Hexagon", "Triangle");

  private XMLValidator() {
    // utility class — not instantiable
  }

  static void validateRequiredField(String value, String fieldName) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Missing required field: " + fieldName);
    }
  }

  static void validateSimulationType(String gameType) {
    if (!VALID_TYPES.contains(gameType)) {
      throw new IllegalArgumentException("Invalid simulation type: " + gameType);
    }
  }

  static void validateCellStates(int[][] cells, Set<Integer> validStates) {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        if (!validStates.contains(cells[i][j])) {
          throw new IllegalArgumentException(
              "Invalid cell state " + cells[i][j] + " at (" + i + ", " + j
                  + "). Valid states: " + validStates);
        }
      }
    }
  }

  static void validateOptionalTopologyField(String value, List<String> validValues,
      String fieldName) {
    if (value != null && !value.isEmpty() && !validValues.contains(value)) {
      throw new IllegalArgumentException(
          "Invalid " + fieldName + ": " + value + ". Valid values: " + validValues);
    }
  }
}
