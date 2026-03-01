package cellsociety.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A 2D grid of {@link Cell} objects that serves as the spatial structure for
 * all simulations.
 *
 * <p>
 * Provides access to individual cells, their 8-directional (Moore) neighbors,
 * and
 * bulk operations like extracting all cell states as a 2D array. All cells are
 * initialized to state
 * 0 by default.
 * </p>
 *
 * <p>
 * <b>Assumptions:</b> Row and column counts must be positive. Out-of-bounds
 * access to
 * {@link #getCell(int, int)} will throw an
 * {@code ArrayIndexOutOfBoundsException}.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Cell}
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * Grid grid = new Grid(10, 10);
 * grid.getCell(0, 0).setState(1);
 * List<Cell> neighbors = grid.getNeighbors(0, 0); // returns up to 3 in-bounds neighbors
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 * @author Michael Li
 */
public class Grid {

  private Cell[][] cells;
  private Deque<Cell[][]> history;
  private Cell[][] initialState;

  private int[][] directions = {
      { -1, -1 }, { -1, 0 }, { -1, 1 },
      { 0, -1 }, { 0, 1 },
      { 1, -1 }, { 1, 0 }, { 1, 1 }
  };

  /**
   * Constructs a grid with the given dimensions, initializing all cells to state
   * 0.
   *
   * @param numRows the number of rows
   * @param numCols the number of columns
   */
  public Grid(int numRows, int numCols) {
    emptyConstruction(numRows, numCols);
    createFirstState(numRows, numCols, null);
  }

  public Grid(int numRows, int numCols, int[][] cellStates) {
    emptyConstruction(numRows, numCols);
    createFirstState(numRows, numCols, cellStates);
  }

  private void emptyConstruction(int numRows, int numCols) {
    cells = new Cell[numRows][numCols];
    defaultInitializeCells();
    history = new ArrayDeque<>();
  }

  /**
   * Set the Deque's first state to the grid coppied from ParseResults
   * 
   * @param rows       number of rows of initialized grid
   * @param cols       number of columns of initialized grid
   * @param cellStates cell States to pass into initialized grid
   */
  private void createFirstState(int rows, int cols, int[][] cellStates) {
    if (cellStates != null) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          getCell(i, j).setState(cellStates[i][j]);
        }
      }
    }
    initialState = copyGrid(cells);
    history.add(copyGrid(cells));
  }

  // TODO: test
  public void revertState() {
    if (history.size() > 1) { // Keep at least the initial state
      history.removeFirst(); // Remove current state
      Cell[][] previousState = history.peekFirst(); // Get previous state
      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells[i].length; j++) {
          cells[i][j].setState(previousState[i][j].getState());
        }
      }
    }
  }

  // TODO: test this and possibly other methods
  public void saveState() {
    if (history.size() >= 20) { // TODO: extract magic number into config file
      history.removeLast();
    }
    history.addFirst(copyGrid(cells));
  }

  private Cell[][] copyGrid(Cell[][] cells) {
    Cell[][] copy = new Cell[cells.length][cells[0].length];
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        copy[i][j] = getCell(i, j).copy();
      }
    }
    return copy;
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
   * Returns all in-bounds Moore neighbors (up to 8) of the cell at the given
   * position. Corner cells
   * return 3 neighbors, edge cells return 5, and interior cells return 8.
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
  public int getColumns() {
    return cells[0].length;
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

  /*
   * Resets the grid to its initial state. Clears the history and sets all cells
   * back to their
   * original states as captured at construction.
   */
  public void resetToInitialState() {
    cells = initialState;
    history = new ArrayDeque<>();
  }
}
