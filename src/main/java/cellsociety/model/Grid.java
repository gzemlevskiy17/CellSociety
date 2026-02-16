package cellsociety.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A 2D grid of {@link Cell} objects that serves as the spatial structure for all simulations.
 *
 * <p>Provides access to individual cells, their 8-directional (Moore) neighbors, and
 * bulk operations like extracting all cell states as a 2D array. All cells are initialized
 * to state 0 by default.</p>
 *
 * <p><b>Assumptions:</b> Row and column counts must be positive. Out-of-bounds access to
 * {@link #getCell(int, int)} will throw an {@code ArrayIndexOutOfBoundsException}.</p>
 *
 * <p><b>Dependencies:</b> {@link Cell}</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * Grid grid = new Grid(10, 10);
 * grid.getCell(0, 0).setState(1);
 * List<Cell> neighbors = grid.getNeighbors(0, 0); // returns up to 3 in-bounds neighbors
 * }</pre>
 *
 * @author Michael Li
 */
public class Grid {
  private Cell[][] cells;
  private int[][] directions = {
      { -1, -1 }, { -1, 0 }, { -1, 1 },
      { 0, -1 }, { 0, 1 },
      { 1, -1 }, { 1, 0 }, { 1, 1 }
  };

  /**
   * Constructs a grid with the given dimensions, initializing all cells to state 0.
   *
   * @param numRows the number of rows
   * @param numCols the number of columns
   */
  public Grid(int numRows, int numCols) {
    cells = new Cell[numRows][numCols];
    defaultInitializeCells();
  }

  /**
   * Returns the cell at the specified position.
   *
   * @param row the row index (0-based)
   * @param col the column index (0-based)
   * @return the {@link Cell} at that position
   */
  public Cell getCell(int row, int col) {
    return cells[row][col];
  }

  /**
   * Returns all in-bounds Moore neighbors (up to 8) of the cell at the given position.
   * Corner cells return 3 neighbors, edge cells return 5, and interior cells return 8.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return a list of neighboring {@link Cell} objects
   */
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

  /**
   * Checks whether the given row and column are within the grid boundaries.
   *
   * @param row the row index to check
   * @param col the column index to check
   * @return true if the position is within bounds
   */
  public boolean isInBounds(int row, int col) {
    return row >= 0 && row < cells.length && col >= 0 && col < cells[0].length;
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return the row count
   */
  public int getRows() {
    return cells.length;
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return the column count
   */
  public int getCols() {
    return cells[0].length;
  }

  /**
   * Returns a snapshot of all current cell states as a 2D int array.
   * Useful for saving the simulation state to XML.
   *
   * @return a new 2D array where each entry is the state of the corresponding cell
   */
  public int[][] getCellStates() {
    int[][] states = new int[getRows()][getCols()];
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++) {
        states[i][j] = cells[i][j].getState();
      }
    }
    return states;
  }

  /**
   * Resets all cells in the grid to state 0. Called during construction.
   */
  public void defaultInitializeCells() {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j] = new Cell(0);
      }
    }
  }

}
