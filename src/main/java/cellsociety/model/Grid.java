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
    defaultInitializeCells();
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

  public int getColumns() {
    return cells[0].length;
  }

  public int[][] getCellStates() {
    int[][] states = new int[getRows()][getColumns()];
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        states[i][j] = cells[i][j].getState();
      }
    }
    return states;
  }

  public void defaultInitializeCells() {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j] = new Cell(0);
      }
    }
  }
}
