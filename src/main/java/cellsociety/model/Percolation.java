package cellsociety.model;

import java.util.List;

/**
 * Implementation of a Percolation-style simulation extending the generic Game.
 *
 * <p>Purpose: Implements rules for percolation where empty cells (state 0) become
 * percolated (state 2) when they have a neighbor in the percolated state.</p>
 *
 * <p>Assumptions: Grid and Cell state encodings are consistent with the rule (0 = empty, 2 =
 * percolated). The Game superclass handles state transitions and save/restore behavior.</p>
 *
 * <p>Dependencies: Grid, Game, Cell.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Grid grid = new Grid(rows, cols);
 * Percolation p = new Percolation(grid);
 * p.step(); // advances one generation using percolation rule
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 */
public class Percolation extends Game {

  //TODO: paper uses a linkedlist with all open cells. for this we would need either a Point class, or for the Cell to have coordinates. discuss

  /**
   * Constructs a Percolation simulation using the provided grid.
   *
   * @param grid the grid backing this Percolation game
   */
  public Percolation(Grid grid) {
    super(grid);
  }

  @Override
  protected void updateGame() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumns(); j++) {
        Cell cell = grid.getCell(i, j);
        if (cell.getState() == 0 && hasPercolatedNeighbor(grid.getNeighbors(i, j))) {
          cell.setNextState(2);
        }
      }
    }
  }

  private boolean hasPercolatedNeighbor(List<Cell> neighbors) {
    for (Cell neighbor : neighbors) {
      if (neighbor.getState() == 2) {
        return true;
      }
    }
    return false;
  }
}
