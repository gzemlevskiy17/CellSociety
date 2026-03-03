package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Schelling's Model of Segregation simulation.
 *
 * <p>
 * Uses three states: 0 (empty), 1 (group A), 2 (group B). Each occupied cell checks whether the
 * ratio of same-type neighbors to total non-empty neighbors meets the
 * {@code satisfactionThreshold}. Dissatisfied agents are randomly relocated to empty cells.
 * </p>
 *
 * <p>
 * <b>Assumptions:</b> Threshold should be in the range [0.0, 1.0]. A cell with
 * no non-empty neighbors is considered satisfied. Population counts are conserved across steps —
 * agents are moved, never created or destroyed.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}
 * </p>
 *
 * @author Michael Li
 * @see Game
 */
public class Segregation extends Game {

  public static final int EMPTY = 0;
  private static final Logger LOG = LogManager.getLogger(Segregation.class);

  private final String[] newVariables = new String[]{"threshold"};

  /**
   * Constructs a Segregation simulation with the given satisfaction threshold.
   *
   * @param grid                  the grid of cells; states should be 0 (empty), 1 (group A), or 2
   *                              (group B)
   * @param satisfactionThreshold minimum ratio of same-type neighbors for a cell to stay put
   */
  public Segregation(Grid grid, double satisfactionThreshold) {
    super(grid);
    setParam("threshold", String.valueOf(satisfactionThreshold));
  }

  public Segregation(Grid grid, Map<String, String> params) {
    super(grid, params);
    double threshold = -1;
    try {
      threshold = Double.parseDouble(getParam(newVariables[0]));
    } catch (Exception e) {
      String message = "Failed to parse param " + newVariables[0];
      LOG.warn(message, e);
      throw new IllegalArgumentException(message, e);
    }
    if (threshold < 0 || threshold > 1) {
      String message = "Illegal ignition probability: " + threshold;
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  protected void updateGame() {
    List<int[]> dissatisfied = new ArrayList<>();
    List<int[]> emptyCells = new ArrayList<>();

    for (int i = 0; i < getGrid().getRows(); i++) {
      for (int j = 0; j < getGrid().getColumns(); j++) {
        Cell cell = getGrid().getCell(i, j);
        if (cell.getState() == EMPTY) {
          emptyCells.add(new int[]{i, j});
        } else if (!isSatisfied(i, j)) {
          dissatisfied.add(new int[]{i, j});
        }
      }
    }

    Collections.shuffle(dissatisfied);
    Collections.shuffle(emptyCells);

    int moves = Math.min(dissatisfied.size(), emptyCells.size());
    for (int m = 0; m < moves; m++) {
      int[] from = dissatisfied.get(m);
      int[] to = emptyCells.get(m);
      getGrid().getCell(to[0], to[1]).setNextState(getGrid().getCell(from[0], from[1]).getState());
      getGrid().getCell(from[0], from[1]).setNextState(EMPTY);
    }

  }

  private boolean isSatisfied(int row, int col) {
    int myState = getGrid().getCell(row, col).getState();
    List<Cell> neighbors = getGrid().getNeighbors(row, col);

    int sameCount = 0;
    int totalCount = 0;
    for (Cell neighbor : neighbors) {
      if (neighbor.getState() != EMPTY) {
        totalCount++;
        if (neighbor.getState() == myState) {
          sameCount++;
        }
      }
    }

    if (totalCount == 0) {
      return true;
    }
    return (double) sameCount / totalCount >= Double.parseDouble(getParam("threshold"));
  }

}
