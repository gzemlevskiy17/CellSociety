package cellsociety.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Schelling's Model of Segregation simulation.
 *
 * <p>Uses three states: 0 (empty), 1 (group A), 2 (group B). Each occupied cell checks
 * whether the ratio of same-type neighbors to total non-empty neighbors meets the
 * {@code satisfactionThreshold}. Dissatisfied agents are randomly relocated to empty cells.</p>
 *
 * <p><b>Assumptions:</b> Threshold should be in the range [0.0, 1.0]. A cell with no
 * non-empty neighbors is considered satisfied. Population counts are conserved across steps
 * — agents are moved, never created or destroyed.</p>
 *
 * <p><b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}</p>
 *
 * @author Michael Li
 * @see Game
 */
public class Segregation extends Game {

  private double satisfactionThreshold;

  /**
   * Constructs a Segregation simulation with the given satisfaction threshold.
   *
   * @param grid the grid of cells; states should be 0 (empty), 1 (group A), or 2 (group B)
   * @param satisfactionThreshold minimum ratio of same-type neighbors for a cell to stay put
   */
  public Segregation(Grid grid, double satisfactionThreshold) {
    super(grid);
    this.satisfactionThreshold = satisfactionThreshold;
  }

  @Override
  protected void updateGame() {
    List<int[]> dissatisfied = new ArrayList<>();
    List<int[]> emptyCells = new ArrayList<>();

    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Cell cell = grid.getCell(i, j);
        if (cell.getState() == 0) {
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
      grid.getCell(to[0], to[1]).setNextState(grid.getCell(from[0], from[1]).getState());
      grid.getCell(from[0], from[1]).setNextState(0);
    }

  }

  private boolean isSatisfied(int row, int col) {
    int myState = grid.getCell(row, col).getState();
    List<Cell> neighbors = grid.getNeighbors(row, col);

    int sameCount = 0;
    int totalCount = 0;
    for (Cell neighbor : neighbors) {
      if (neighbor.getState() != 0) {
        totalCount++;
        if (neighbor.getState() == myState) {
          sameCount++;
        }
      }
    }

    if (totalCount == 0) {
      return true;
    }
    return (double) sameCount / totalCount >= satisfactionThreshold;
  }

}
