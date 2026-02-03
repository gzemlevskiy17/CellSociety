package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

public class Grid {
  private Cell[][] cells;
  //TODO: handle incorrect r, c?

  public Grid(int numRows, int numCols) {
    cells = new Cell[numRows][numCols];
    defaultInitializeCells();
  }

  public Cell getCell(int row, int col) {
    return cells[row][col];
  }


  public List<Cell> getNeighbors(int row, int col) {
    List<Cell> neighbors = new ArrayList<>();
    for (int i = row - 1; i <= row + 1; i++) {
      for (int j = col - 1; j <= col + 1; j++) {
        if (isInBounds(i, j) && !(i == row && j == col)) {
          neighbors.add(cells[i][j]);
        }
      }
    }
    return neighbors;
  }

  public boolean isInBounds(int row, int col) {
    return row >= 0 && row < cells.length && col >= 0 && col < cells[0].length;
  }

  public void defaultInitializeCells() {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j] = new Cell(0);
      }
    }
  }

  //TODO: set cell state?
}
