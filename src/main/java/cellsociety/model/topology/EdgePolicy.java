package cellsociety.model.topology;

/**
 * Strategy interface for resolving out-of-bounds neighbor positions.
 *
 * <p>
 * When {@link cellsociety.model.Grid} computes neighbors, candidate positions
 * may fall outside the grid boundaries. Implementations decide what to do:
 * skip the position, wrap around, or reflect it back.
 * </p>
 *
 * @author Michael Li
 */
public interface EdgePolicy {

  /**
   * Resolves a candidate neighbor position to a valid grid position.
   *
   * @param row  the candidate row (may be out of bounds)
   * @param col  the candidate column (may be out of bounds)
   * @param rows total number of rows in the grid
   * @param cols total number of columns in the grid
   * @return a two-element array {resolvedRow, resolvedCol}, or {@code null} to skip this neighbor
   */
  int[] resolve(int row, int col, int rows, int cols);
}
