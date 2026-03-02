package cellsociety.model.topology.neighborhood;

/**
 * Von Neumann neighborhood: only the 4 orthogonal neighbors (up, down, left, right).
 *
 * @author Michael Li
 */
public class VonNeumannNeighborhood implements NeighborhoodStrategy {

  private static final int[][] OFFSETS = {
      {-1, 0},
      { 0, -1}, { 0, 1},
      { 1, 0}
  };

  @Override
  public int[][] getOffsets(int row, int col) {
    return OFFSETS;
  }
}
