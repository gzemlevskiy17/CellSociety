package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

public class Grid {
  private Cell[][] cells;
  private int[][] directions = {
      { -1, -1 }, { -1, 0 }, { -1, 1 },
      { 0, -1 }, { 0, 1 },
      { 1, -1 }, { 1, 0 }, { 1, 1 }
  };

  public Grid(int numRows, int numCols) {
    cells = new Cell[numRows][numCols];
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        cells[i][j] = new Cell(0);
      }
    }
  }

  public Cell getCell(int row, int col) {
    return cells[row][col];
  }

  public List<Cell> getNeighbors(int row, int col) {
    List<Cell> neighbors = new ArrayList<>();
    for (int[] direction : directions) {
      int nr = row + direction[0];
      int nc = col + direction[1];

      if (isInBounds(nr, nc)) {
        neighbors.add(cells[nr][nc]);
      }
    }
    return neighbors;
  }

  public boolean isInBounds(int row, int col) {
    return row >= 0 && row < cells.length && col >= 0 && col < cells[0].length;
  }

  public int getRows() {
    return cells.length;
  }

  public int getCols() {
    return cells[0].length;
  }

  // TODO: set cell state?
}
