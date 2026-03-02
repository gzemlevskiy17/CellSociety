package cellsociety.model;

import cellsociety.model.topology.edgebehavior.BoundedEdge;
import cellsociety.model.topology.edgebehavior.EdgePolicy;
import cellsociety.model.topology.neighborhood.MooreNeighborhood;
import cellsociety.model.topology.neighborhood.NeighborhoodStrategy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A 2D grid of {@link Cell} objects that serves as the spatial structure for
 * all simulations.
 *
 * <p>
 * Neighbor lookup is fully configurable via a {@link NeighborhoodStrategy}
 * (which directions to check) and an {@link EdgePolicy} (what to do at the
 * boundary). Both default to Moore + Bounded if not specified, preserving
 * existing behavior.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Cell}, {@link NeighborhoodStrategy}, {@link EdgePolicy}
 * </p>
 *
 * @author Georgiy Zemlevskiy
 * @author Michael Li
 */
public class Grid {

  private static final int MAX_HISTORY = 20;

  private Cell[][] cells;
  private Deque<Cell[][]> history;
  private Cell[][] initialState;

  private final NeighborhoodStrategy neighborhoodStrategy;
  private final EdgePolicy edgePolicy;

  /**
   * Constructs a grid with default Moore neighborhood and bounded edges.
   *
   * @param numRows the number of rows
   * @param numCols the number of columns
   */
  public Grid(int numRows, int numCols) {
    this(numRows, numCols, null, new MooreNeighborhood(), new BoundedEdge());
  }

  /**
   * Constructs a grid with initial cell states, default Moore neighborhood and bounded edges.
   *
   * @param numRows    the number of rows
   * @param numCols    the number of columns
   * @param cellStates initial cell states, or null for all-zero
   */
  public Grid(int numRows, int numCols, int[][] cellStates) {
    this(numRows, numCols, cellStates, new MooreNeighborhood(), new BoundedEdge());
  }

  /**
   * Constructs a grid with explicit neighborhood and edge strategies.
   *
   * @param numRows      the number of rows
   * @param numCols      the number of columns
   * @param cellStates   initial cell states, or null for all-zero
   * @param neighborhood the strategy defining which offsets count as neighbors
   * @param edgePolicy   the policy defining how out-of-bounds positions are resolved
   */
  public Grid(int numRows, int numCols, int[][] cellStates,
      NeighborhoodStrategy neighborhood, EdgePolicy edgePolicy) {
    this.neighborhoodStrategy = neighborhood;
    this.edgePolicy = edgePolicy;
    cells = new Cell[numRows][numCols];
    defaultInitializeCells();
    history = new ArrayDeque<>();
    createFirstState(numRows, numCols, cellStates);
  }

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

  /**
   * Returns all neighbors of the cell at (row, col) as determined by the
   * current {@link NeighborhoodStrategy} and {@link EdgePolicy}.
   *
   * @param row the row index of the target cell
   * @param col the column index of the target cell
   * @return a list of neighboring {@link Cell} objects
   */
  public List<Cell> getNeighbors(int row, int col) {
    List<Cell> neighbors = new ArrayList<>();
    for (int[] offset : neighborhoodStrategy.getOffsets(row, col)) {
      int[] resolved = edgePolicy.resolve(
          row + offset[0], col + offset[1], getRows(), getColumns());
      if (resolved != null) {
        neighbors.add(cells[resolved[0]][resolved[1]]);
      }
    }
    return neighbors;
  }

  public void revertState() {
    if (history.size() > 1) {
      history.removeFirst();
      Cell[][] previousState = history.peekFirst();
      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells[i].length; j++) {
          cells[i][j].setState(previousState[i][j].getState());
        }
      }
    }
  }

  public void saveState() {
    if (history.size() >= MAX_HISTORY) {
      history.removeLast();
    }
    history.addFirst(copyGrid(cells));
  }

  private Cell[][] copyGrid(Cell[][] source) {
    Cell[][] copy = new Cell[source.length][source[0].length];
    for (int i = 0; i < source.length; i++) {
      for (int j = 0; j < source[i].length; j++) {
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
   * Resets all cells in the grid to state 0.
   */
  public void defaultInitializeCells() {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        cells[i][j] = new Cell(0);
      }
    }
  }

  /**
   * Resets the grid to its initial state and clears the history.
   */
  public void resetToInitialState() {
    cells = initialState;
    history = new ArrayDeque<>();
  }
}
