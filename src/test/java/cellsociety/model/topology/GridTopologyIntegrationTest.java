package cellsociety.model.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.Grid;
import org.junit.jupiter.api.Test;

/**
 * Integration tests verifying that {@link Grid} uses the correct
 * neighborhood and edge strategies together to produce the expected
 * neighbor counts for corner, edge, and interior cells.
 *
 * @author Michael Li
 */
class GridTopologyIntegrationTest {

  // ─── BoundedEdge + MooreNeighborhood (default) ───────────────────────────────

  @Test
  void bounded_moore_cornerCellHas3Neighbors() {
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new BoundedEdge());
    assertEquals(3, grid.getNeighbors(0, 0).size());
  }

  @Test
  void bounded_moore_edgeCellHas5Neighbors() {
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new BoundedEdge());
    assertEquals(5, grid.getNeighbors(0, 2).size());
  }

  @Test
  void bounded_moore_interiorCellHas8Neighbors() {
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new BoundedEdge());
    assertEquals(8, grid.getNeighbors(2, 2).size());
  }

  // ─── ToroidalEdge + MooreNeighborhood ────────────────────────────────────────

  @Test
  void toroidal_moore_cornerCellHas8Neighbors() {
    // With wrapping, every cell — even corners — has 8 neighbors
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new ToroidalEdge());
    assertEquals(8, grid.getNeighbors(0, 0).size());
  }

  @Test
  void toroidal_moore_edgeCellHas8Neighbors() {
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new ToroidalEdge());
    assertEquals(8, grid.getNeighbors(0, 2).size());
  }

  @Test
  void toroidal_moore_wrappedNeighborIsActualGridCell() {
    // Top-left corner's top neighbor should wrap to the bottom row
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new ToroidalEdge());
    // (0,0)'s neighbor at offset (-1,0) = row 4, col 0
    assertEquals(grid.getCell(4, 0), grid.getNeighbors(0, 0).stream()
        .filter(c -> c == grid.getCell(4, 0))
        .findFirst().orElse(null));
  }

  // ─── MirrorEdge + MooreNeighborhood ──────────────────────────────────────────

  @Test
  void mirror_moore_cornerCellHas8Neighbors() {
    // With mirroring, every cell has 8 entries (boundary cell may appear multiple times)
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new MirrorEdge());
    assertEquals(8, grid.getNeighbors(0, 0).size());
  }

  @Test
  void mirror_moore_interiorCellHas8Neighbors() {
    Grid grid = new Grid(5, 5, null, new MooreNeighborhood(), new MirrorEdge());
    assertEquals(8, grid.getNeighbors(2, 2).size());
  }

  // ─── BoundedEdge + VonNeumannNeighborhood ────────────────────────────────────

  @Test
  void bounded_vonNeumann_cornerCellHas2Neighbors() {
    Grid grid = new Grid(5, 5, null, new VonNeumannNeighborhood(), new BoundedEdge());
    assertEquals(2, grid.getNeighbors(0, 0).size());
  }

  @Test
  void bounded_vonNeumann_interiorCellHas4Neighbors() {
    Grid grid = new Grid(5, 5, null, new VonNeumannNeighborhood(), new BoundedEdge());
    assertEquals(4, grid.getNeighbors(2, 2).size());
  }

  // ─── BoundedEdge + ExtendedMooreNeighborhood ─────────────────────────────────

  @Test
  void bounded_extendedMoore_interiorCellHas24Neighbors() {
    // Cell far from all edges in a large grid should have all 24 neighbors
    Grid grid = new Grid(10, 10, null, new ExtendedMooreNeighborhood(), new BoundedEdge());
    assertEquals(24, grid.getNeighbors(5, 5).size());
  }

  @Test
  void bounded_extendedMoore_cornerCellFewerThan24Neighbors() {
    Grid grid = new Grid(10, 10, null, new ExtendedMooreNeighborhood(), new BoundedEdge());
    // Corner only sees cells that are in bounds — should be less than 24
    int count = grid.getNeighbors(0, 0).size();
    assertEquals(true, count < 24, "Corner cell should have fewer than 24 neighbors when bounded");
  }

  // ─── BoundedEdge + HexagonalNeighborhood ─────────────────────────────────────

  @Test
  void bounded_hexagonal_evenRowInteriorCellHas6Neighbors() {
    Grid grid = new Grid(10, 10, null, new HexagonalNeighborhood(), new BoundedEdge());
    // Row 4, col 5 is an even row — all 6 hex offsets in bounds
    assertEquals(6, grid.getNeighbors(4, 5).size());
  }

  @Test
  void bounded_hexagonal_oddRowInteriorCellHas6Neighbors() {
    Grid grid = new Grid(10, 10, null, new HexagonalNeighborhood(), new BoundedEdge());
    assertEquals(6, grid.getNeighbors(5, 5).size());
  }

  // ─── BoundedEdge + TriangularNeighborhood ────────────────────────────────────

  @Test
  void bounded_triangular_pointingUpInteriorCellHas3Neighbors() {
    // (row+col) % 2 == 0 → pointing up: neighbors left, right, below
    Grid grid = new Grid(10, 10, null, new TriangularNeighborhood(), new BoundedEdge());
    assertEquals(3, grid.getNeighbors(4, 4).size()); // 4+4=8, even → pointing up
  }

  @Test
  void bounded_triangular_pointingDownInteriorCellHas3Neighbors() {
    // (row+col) % 2 == 1 → pointing down: neighbors left, right, above
    Grid grid = new Grid(10, 10, null, new TriangularNeighborhood(), new BoundedEdge());
    assertEquals(3, grid.getNeighbors(4, 5).size()); // 4+5=9, odd → pointing down
  }
}
