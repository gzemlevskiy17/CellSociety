package cellsociety.model;

import java.util.List;

public class ForestFire extends Game {

  private double ignitionProbability;
  private double fillProbability;

  public ForestFire(Grid grid, double ignitionProbability, double fillProbability) {
    super(grid);
    this.ignitionProbability = ignitionProbability;
    this.fillProbability = fillProbability;
  }

  @Override
  public void updateGame() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Cell cell = grid.getCell(i, j);
        List<Cell> neighbors = grid.getNeighbors(i, j);

        if (cell.getState() == 2) {
          cell.setNextState(0);
        }

        if (cell.getState() == 1) {
//          System.err.println("Checking neighbors for cell at (" + i + ", " + j + ")");
          for (Cell neighbor : neighbors) {
            if (neighbor.getState() == 2) {
              cell.setNextState(2);
//              System.out.println("Cell at (" + i + ", " + j + ") caught fire.");
              break;
            }
          }
          if (cell.getNextState() == 1) {
            if (Math.random() < ignitionProbability) {
              cell.setNextState(2);
            }
          }
        }

        if (cell.getState() == 0) {
          if (Math.random() < fillProbability) {
            cell.setNextState(1);
          }
        }
      }
    }
    commitChanges();
  }

  private void commitChanges() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        grid.getCell(i, j).updateState();
      }
    }
  }
}
