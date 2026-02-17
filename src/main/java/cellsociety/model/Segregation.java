package cellsociety.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Segregation extends Game {

  private double satisfactionThreshold;

  public Segregation(Grid grid, double satisfactionThreshold) {
    super(grid);
    this.satisfactionThreshold = satisfactionThreshold;
  }

  @Override
  protected void updateGame() {
    List<int[]> dissatisfied = new ArrayList<>();
    List<int[]> emptyCells = new ArrayList<>();

    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumns(); j++) {
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
