package cellsociety.model.topology.edgebehavior;

/**
 * Bounded edge policy: neighbors outside the grid boundary are skipped.
 *
 * <p>
 * This is the default behavior. Corner cells have fewer neighbors than
 * interior cells.
 * </p>
 *
 * @author Michael Li
 */
public class BoundedEdge implements EdgePolicy {

  @Override
  public int[] resolve(int row, int col, int rows, int cols) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      return null;
    }
    return new int[]{row, col};
  }
}
