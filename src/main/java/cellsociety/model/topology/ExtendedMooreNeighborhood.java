package cellsociety.model.topology;

/**
 * Extended Moore neighborhood: all cells within 2 steps (a 5x5 area minus the center cell).
 * Produces 24 neighbors for interior cells.
 *
 * @author Michael Li
 */
public class ExtendedMooreNeighborhood implements NeighborhoodStrategy {

  private static final int[][] OFFSETS = buildOffsets();

  private static int[][] buildOffsets() {
    int[][] offsets = new int[24][2];
    int index = 0;
    for (int dr = -2; dr <= 2; dr++) {
      for (int dc = -2; dc <= 2; dc++) {
        if (dr != 0 || dc != 0) {
          offsets[index][0] = dr;
          offsets[index][1] = dc;
          index++;
        }
      }
    }
    return offsets;
  }

  @Override
  public int[][] getOffsets(int row, int col) {
    return OFFSETS;
  }
}
