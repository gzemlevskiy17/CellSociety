package cellsociety.model.topology;

/**
 * Triangular neighborhood: 3 edge-sharing neighbors for alternating up/down triangles.
 *
 * <p>
 * Triangles alternate orientation based on (row + col) % 2:
 * <ul>
 *   <li>Even (pointing up): neighbors are left, right, and the cell below.</li>
 *   <li>Odd (pointing down): neighbors are left, right, and the cell above.</li>
 * </ul>
 * </p>
 *
 * @author Michael Li
 */
public class TriangularNeighborhood implements NeighborhoodStrategy {

  private static final int[][] POINTING_UP_OFFSETS = {
      { 0, -1}, { 0, 1},
      { 1,  0}
  };

  private static final int[][] POINTING_DOWN_OFFSETS = {
      {-1,  0},
      { 0, -1}, { 0, 1}
  };

  @Override
  public int[][] getOffsets(int row, int col) {
    return ((row + col) % 2 == 0) ? POINTING_UP_OFFSETS : POINTING_DOWN_OFFSETS;
  }
}
