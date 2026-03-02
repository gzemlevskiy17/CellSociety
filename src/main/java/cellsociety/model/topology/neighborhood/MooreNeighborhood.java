package cellsociety.model.topology.neighborhood;

/**
 * Moore neighborhood: all 8 surrounding cells (including diagonals).
 *
 * @author Michael Li
 */
public class MooreNeighborhood implements NeighborhoodStrategy {

  private static final int[][] OFFSETS = {
      {-1, -1}, {-1, 0}, {-1, 1},
      { 0, -1},           { 0, 1},
      { 1, -1}, { 1, 0}, { 1, 1}
  };

  @Override
  public int[][] getOffsets(int row, int col) {
    return OFFSETS;
  }
}
