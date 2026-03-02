package cellsociety.model.topology.edgebehavior;

/**
 * Mirror edge policy: out-of-bounds positions are reflected back to the nearest edge cell.
 *
 * <p>
 * Edge cells act as mirrors — a neighbor position that falls outside the grid
 * is clamped to the boundary, so the boundary cell itself is used as that
 * neighbor. All cells always have a full neighborhood.
 * </p>
 *
 * @author Michael Li
 */
public class MirrorEdge implements EdgePolicy {

  @Override
  public int[] resolve(int row, int col, int rows, int cols) {
    int r = Math.max(0, Math.min(rows - 1, row));
    int c = Math.max(0, Math.min(cols - 1, col));
    return new int[]{r, c};
  }
}
