package cellsociety.model;

/**
 * Read-only accessor for a {@link Grid}, providing cell-by-cell access without
 * exposing the underlying data structure.
 *
 * <p>
 * The controller layer imports this class instead of {@link Grid} directly,
 * ensuring the model's internal representation (e.g., a 2D array) cannot be
 * modified or leaked through the API boundary.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Grid}
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 *
 * <pre>{@code
 * GridReader reader = game.getGridReader();
 * for (int r = 0; r < reader.getRows(); r++) {
 *     for (int c = 0; c < reader.getCols(); c++) {
 *         int state = reader.getCellState(r, c);
 *     }
 * }
 * }</pre>
 *
 * @author Michael Li
 */
public class GridReader {

  private final Grid grid;

  /**
   * Constructs a GridReader that wraps the given grid.
   *
   * @param grid the grid to read from; must not be null
   */
  public GridReader(Grid grid) {
    this.grid = grid;
  }

  /**
   * Returns the state of the cell at the given position.
   *
   * @param row the row index (0-based)
   * @param col the column index (0-based)
   * @return the integer state of that cell
   */
  public int getCellState(int row, int col) {
    return grid.getCell(row, col).getState();
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return the row count
   */
  public int getRows() {
    return grid.getRows();
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return the column count
   */
  public int getCols() {
    return grid.getColumns();
  }
}
