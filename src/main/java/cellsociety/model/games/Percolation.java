package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a Percolation-style simulation extending the generic Game.
 *
 * <p>
 * Purpose: Implements rules for percolation where empty cells (state 0) become percolated (state 2)
 * when they have a neighbor in the percolated state.
 * </p>
 *
 * <p>
 * Assumptions: Grid and Cell state encodings are consistent with the rule (0 = empty, 2 =
 * percolated). The Game superclass handles state transitions and save/restore behavior. The blocked
 * state does not need to be checked for because those cells will never change.
 * </p>
 *
 * <p>
 * Dependencies: Grid, Game, Cell.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * Grid grid = new Grid(rows, cols);
 * Percolation p = new Percolation(grid);
 * p.step(); // advances one generation using percolation rule
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 */
public class Percolation extends Game {

  public static final int EMPTY = 0;
  public static final int PERCOLATED = 2;

  // TODO: paper uses a linkedlist with all open cells. for this we would need
  // either a Point class, or for the Cell to have coordinates. discuss

  /**
   * Constructs a Percolation simulation using the provided getGrid().
   *
   * @param grid the grid backing this Percolation game
   */
  public Percolation(Grid grid) {
    super(grid);
  }

  public Percolation(Grid grid, Map<String, String> params) {
    super(grid, params);
  }

  @Override
  protected void updateGame() {
    for (int i = 0; i < getGrid().getRows(); i++) {
      for (int j = 0; j < getGrid().getColumns(); j++) {
        Cell cell = getGrid().getCell(i, j);
        if (cell.getState() == EMPTY && hasPercolatedNeighbor(getGrid().getNeighbors(i, j))) {
          cell.setNextState(PERCOLATED);
        }
      }
    }
  }

  private boolean hasPercolatedNeighbor(List<Cell> neighbors) {
    for (Cell neighbor : neighbors) {
      if (neighbor.getState() == PERCOLATED) {
        return true;
      }
    }
    return false;
  }
}
