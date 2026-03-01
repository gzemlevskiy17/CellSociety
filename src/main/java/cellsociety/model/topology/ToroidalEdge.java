package cellsociety.model.topology;

/**
 * Toroidal edge policy: the grid wraps around in both directions.
 *
 * <p>
 * The left edge connects to the right edge and the top connects to the bottom,
 * creating a continuous surface with no fixed boundaries. All cells always
 * have a full neighborhood.
 * </p>
 *
 * @author Michael Li
 */
public class ToroidalEdge implements EdgePolicy {

  @Override
  public int[] resolve(int row, int col, int rows, int cols) {
    int r = ((row % rows) + rows) % rows;
    int c = ((col % cols) + cols) % cols;
    return new int[]{r, c};
  }
}
