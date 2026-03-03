package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Forest Fire spreading simulation.
 *
 * <p>
 * Uses three states: 0 (empty), 1 (tree), 2 (burning). Transition rules:
 * </p>
 * <ul>
 * <li>A burning cell becomes empty.</li>
 * <li>A tree catches fire if any neighbor is burning, or spontaneously with
 * probability {@code ignitionProbability}.</li>
 * <li>An empty cell grows a tree with probability {@code fillProbability}.</li>
 * </ul>
 *
 * <p>
 * <b>Assumptions:</b> Probabilities should be in the range [0.0, 1.0].
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}
 * </p>
 *
 * @author Georgiy Zemlevskiy
 * @see Game
 */
public class ForestFire extends Game {

  public static final int EMPTY = 0;
  public static final int TREE = 1;
  public static final int BURNING = 2;
  private static final Logger LOG = LogManager.getLogger(ForestFire.class);
  private final String[] newVariables = new String[]{"ignitionProbability",
      "fillProbability"}; //TODO: replace with property file

  /**
   * Constructs a Forest Fire simulation with the given probabilities.
   *
   * @param grid                the grid of cells; states should be 0 (empty), 1 (tree), or 2
   *                            (burning)
   * @param ignitionProbability chance a tree spontaneously catches fire each step
   * @param fillProbability     chance an empty cell grows a new tree each step
   */
  public ForestFire(Grid grid, double ignitionProbability, double fillProbability) {
    super(grid);

    validateParam(newVariables[0], ignitionProbability, new double[]{0, 1});
    setParam(newVariables[0], String.valueOf(ignitionProbability));

    validateParam(newVariables[1], fillProbability, new double[]{0, 1});
    setParam(newVariables[1], String.valueOf(fillProbability));
  }

  public ForestFire(Grid grid, Map<String, String> params) {
    super(grid, params);
    double probability = -1;
    for (String vari : newVariables) {
      try {
        probability = Double.parseDouble(getParam(vari));
      } catch (Exception e) {
        throw new IllegalArgumentException(
            "Illegal " + vari + ": " + getParam(vari)); //TODO: understand
      }
      if (probability < 0 || probability > 1) {
        throw new IllegalArgumentException("Illegal " + vari + ": " + getParam(vari));
      }
    }
  }

  private void validateParam(String parameterName, double parameter, double[] bounds) {
    if (parameter < bounds[0] || parameter > bounds[1]) {
      String message = String.format("Illegal %s probability: " + parameter, parameterName);
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public void updateGame() {
    for (int i = 0; i < getGrid().getRows(); i++) {
      for (int j = 0; j < getGrid().getColumns(); j++) {
        Cell cell = getGrid().getCell(i, j);
        List<Cell> neighbors = getGrid().getNeighbors(i, j);
        updateCell(cell, neighbors);
      }
    }
  }

  private void updateCell(Cell cell, List<Cell> neighbors) {
    if (cell.getState() == BURNING) {
      cell.setNextState(EMPTY);
    }

    if (cell.getState() == TREE) {
      for (Cell neighbor : neighbors) {
        if (neighbor.getState() == BURNING) {
          cell.setNextState(BURNING);
          break;
        }
      }
      if (cell.getNextState() == TREE) {
        if (Math.random() < Double.parseDouble(getParam("ignitionProbability"))) {
          cell.setNextState(BURNING);
        }
      }
    }

    if (cell.getState() == EMPTY) {
      if (Math.random() < Double.parseDouble(getParam("fillProbability"))) {
        cell.setNextState(TREE);
      }
    }
  }
}
