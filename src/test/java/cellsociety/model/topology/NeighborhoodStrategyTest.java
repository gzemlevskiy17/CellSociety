package cellsociety.model.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for all five NeighborhoodStrategy implementations.
 *
 * <p>Verifies that each strategy returns the correct number of offsets and that
 * the offsets vary correctly with row/column for position-dependent strategies.</p>
 *
 * @author Michael Li
 */
class NeighborhoodStrategyTest {

  // ─── MooreNeighborhood ────────────────────────────────────────────────────────

  @Test
  void moore_alwaysReturns8Offsets() {
    MooreNeighborhood n = new MooreNeighborhood();
    assertEquals(8, n.getOffsets(0, 0).length);
  }

  @Test
  void moore_offsetsUnchangedForDifferentPositions() {
    MooreNeighborhood n = new MooreNeighborhood();
    assertEquals(n.getOffsets(0, 0).length, n.getOffsets(3, 4).length);
  }

  // ─── VonNeumannNeighborhood ───────────────────────────────────────────────────

  @Test
  void vonNeumann_alwaysReturns4Offsets() {
    VonNeumannNeighborhood n = new VonNeumannNeighborhood();
    assertEquals(4, n.getOffsets(0, 0).length);
  }

  @Test
  void vonNeumann_noDiagonalOffsets() {
    VonNeumannNeighborhood n = new VonNeumannNeighborhood();
    for (int[] offset : n.getOffsets(0, 0)) {
      // An offset is diagonal iff both dr and dc are non-zero
      assertEquals(false, offset[0] != 0 && offset[1] != 0,
          "VonNeumann should not include diagonal offset [" + offset[0] + "," + offset[1] + "]");
    }
  }

  // ─── ExtendedMooreNeighborhood ────────────────────────────────────────────────

  @Test
  void extendedMoore_alwaysReturns24Offsets() {
    ExtendedMooreNeighborhood n = new ExtendedMooreNeighborhood();
    assertEquals(24, n.getOffsets(5, 5).length);
  }

  @Test
  void extendedMoore_noZeroZeroOffset() {
    ExtendedMooreNeighborhood n = new ExtendedMooreNeighborhood();
    for (int[] offset : n.getOffsets(5, 5)) {
      assertEquals(false, offset[0] == 0 && offset[1] == 0,
          "ExtendedMoore must not include the [0,0] (self) offset");
    }
  }

  @Test
  void extendedMoore_allOffsetsWithinRadius2() {
    ExtendedMooreNeighborhood n = new ExtendedMooreNeighborhood();
    for (int[] offset : n.getOffsets(0, 0)) {
      assertEquals(true, Math.abs(offset[0]) <= 2 && Math.abs(offset[1]) <= 2,
          "All ExtendedMoore offsets should be within -2..2");
    }
  }

  // ─── HexagonalNeighborhood ────────────────────────────────────────────────────

  @Test
  void hexagonal_alwaysReturns6Offsets() {
    HexagonalNeighborhood n = new HexagonalNeighborhood();
    assertEquals(6, n.getOffsets(0, 0).length);
    assertEquals(6, n.getOffsets(1, 0).length);
  }

  @Test
  void hexagonal_evenRowDiffersFromOddRow() {
    HexagonalNeighborhood n = new HexagonalNeighborhood();
    int[][] evenOffsets = n.getOffsets(0, 0);
    int[][] oddOffsets = n.getOffsets(1, 0);
    // The two offset arrays should be different objects (different offset values)
    boolean allSame = true;
    for (int i = 0; i < 6; i++) {
      if (evenOffsets[i][0] != oddOffsets[i][0] || evenOffsets[i][1] != oddOffsets[i][1]) {
        allSame = false;
        break;
      }
    }
    assertEquals(false, allSame, "Even-row and odd-row hex offsets should differ");
  }

  // ─── TriangularNeighborhood ───────────────────────────────────────────────────

  @Test
  void triangular_alwaysReturns3Offsets() {
    TriangularNeighborhood n = new TriangularNeighborhood();
    assertEquals(3, n.getOffsets(0, 0).length); // even: pointing up
    assertEquals(3, n.getOffsets(0, 1).length); // odd: pointing down
  }

  @Test
  void triangular_pointingUpHasDownNeighbor() {
    // (row+col) % 2 == 0 → pointing up → one neighbor in row+1 direction
    TriangularNeighborhood n = new TriangularNeighborhood();
    int[][] offsets = n.getOffsets(0, 0); // even parity
    boolean hasRowPlusOne = false;
    for (int[] offset : offsets) {
      if (offset[0] == 1 && offset[1] == 0) {
        hasRowPlusOne = true;
      }
    }
    assertEquals(true, hasRowPlusOne, "Pointing-up triangle should have a neighbor below (dr=+1)");
  }

  @Test
  void triangular_pointingDownHasUpNeighbor() {
    // (row+col) % 2 == 1 → pointing down → one neighbor in row-1 direction
    TriangularNeighborhood n = new TriangularNeighborhood();
    int[][] offsets = n.getOffsets(0, 1); // odd parity
    boolean hasRowMinusOne = false;
    for (int[] offset : offsets) {
      if (offset[0] == -1 && offset[1] == 0) {
        hasRowMinusOne = true;
      }
    }
    assertEquals(true, hasRowMinusOne, "Pointing-down triangle should have a neighbor above (dr=-1)");
  }
}
