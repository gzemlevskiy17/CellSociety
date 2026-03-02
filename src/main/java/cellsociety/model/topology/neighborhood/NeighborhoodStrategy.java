package cellsociety.model.topology.neighborhood;

/**
 * Strategy interface for determining which cell offsets constitute a neighborhood.
 *
 * <p>
 * Implementations return the set of [rowOffset, colOffset] pairs to check
 * relative to a given cell. The position is provided so that position-dependent
 * neighborhoods (e.g., hexagonal) can be supported without special-casing in
 * {@link cellsociety.model.Grid}.
 * </p>
 *
 * @author Michael Li
 */
public interface NeighborhoodStrategy {

  /**
   * Returns the offsets to apply to a cell at (row, col) to find its neighbors.
   *
   * @param row the row of the cell whose neighbors are being computed
   * @param col the column of the cell whose neighbors are being computed
   * @return a 2D array where each entry is {rowOffset, colOffset}
   */
  int[][] getOffsets(int row, int col);
}
