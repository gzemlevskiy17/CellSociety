package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;

import java.util.List;

public class Percolation extends Game {

  //TODO: paper uses a linkedlist with all open cells. for this we would need either a Point class, or for the Cell to have coordinates. discuss
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
