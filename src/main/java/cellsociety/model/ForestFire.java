package cellsociety.model;

import java.util.List;

/**
 * Forest Fire spreading simulation.
 *
 * <p>Uses three states: 0 (empty), 1 (tree), 2 (burning). Transition rules:</p>
 * <ul>
 *   <li>A burning cell becomes empty.</li>
 *   <li>A tree catches fire if any neighbor is burning, or spontaneously with
 *       probability {@code ignitionProbability}.</li>
 *   <li>An empty cell grows a tree with probability {@code fillProbability}.</li>
 * </ul>
 *
 * <p><b>Assumptions:</b> Probabilities should be in the range [0.0, 1.0].</p>
 *
 * <p><b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}</p>
 *
 * @author Michael Li
 * @see Game
 */
public class ForestFire extends Game {

  private double ignitionProbability;
  private double fillProbability;

  /**
   * Constructs a Forest Fire simulation with the given probabilities.
   *
   * @param grid the grid of cells; states should be 0 (empty), 1 (tree), or 2 (burning)
   * @param ignitionProbability chance a tree spontaneously catches fire each step
   * @param fillProbability chance an empty cell grows a new tree each step
   */
  public ForestFire(Grid grid, double ignitionProbability, double fillProbability) {
    super(grid);
    if (ignitionProbability >= 0 && ignitionProbability <= 1) {
      this.ignitionProbability = ignitionProbability;
//      System.out.println("Ignition Probability: " + ignitionProbability);
    } else {
      throw new IllegalArgumentException("Illegal ignition probability: " + ignitionProbability);
    }

    if (fillProbability >= 0 && fillProbability <= 1) {
      this.fillProbability = fillProbability;
//      System.out.println("Fill Probability: " + fillProbability);
    } else {
      throw new IllegalArgumentException("Illegal fill probability: " + fillProbability);
    }
  }

  @Override
  public void updateGame() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumns(); j++) {
        Cell cell = grid.getCell(i, j);
        List<Cell> neighbors = grid.getNeighbors(i, j);
        updateCell(cell, neighbors);
      }
    }
  }

  private void updateCell(Cell cell, List<Cell> neighbors) {
    if (cell.getState() == 2) {
      cell.setNextState(0);
    }

    if (cell.getState() == 1) {
      for (Cell neighbor : neighbors) {
        if (neighbor.getState() == 2) {
          cell.setNextState(2);
          break;
        }
      }
      if (cell.getNextState() == 1) {
        if (Math.random() < ignitionProbability) {
//          System.out.println("ignition");
          cell.setNextState(2);
        }
      }
    }

    if (cell.getState() == 0) {
      if (Math.random() < fillProbability) {
//        System.out.println("fill");
        cell.setNextState(1);
      }
    }
  }
}
