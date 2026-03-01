package cellsociety.model.topology.neighborhood;

/**
 * Hexagonal neighborhood: 6 neighbors using offset coordinates.
 *
 * <p>
 * Because hex cells are stored in a rectangular array, which 6 cells count as
 * neighbors depends on whether the row is even or odd.
 * </p>
 *
 * @author Michael Li
 */
public class HexagonalNeighborhood implements NeighborhoodStrategy {

  private static final int[][] EVEN_ROW_OFFSETS = {
      {-1, -1}, {-1, 0},
      { 0, -1}, { 0, 1},
      { 1, -1}, { 1, 0}
  };

  private static final int[][] ODD_ROW_OFFSETS = {
      {-1, 0}, {-1, 1},
      { 0, -1}, { 0, 1},
      { 1, 0}, { 1, 1}
  };

  @Override
  public int[][] getOffsets(int row, int col) {
    return (row % 2 == 0) ? EVEN_ROW_OFFSETS : ODD_ROW_OFFSETS;
  }
}
