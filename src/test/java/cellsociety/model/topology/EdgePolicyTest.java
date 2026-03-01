package cellsociety.model.topology;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests for all three EdgePolicy implementations: BoundedEdge, ToroidalEdge, MirrorEdge.
 *
 * @author Michael Li
 */
class EdgePolicyTest {

  // ─── BoundedEdge ─────────────────────────────────────────────────────────────

  @Test
  void boundedEdge_inBoundsReturnsCell() {
    BoundedEdge edge = new BoundedEdge();
    assertArrayEquals(new int[]{2, 3}, edge.resolve(2, 3, 5, 5));
  }

  @Test
  void boundedEdge_negativeRowReturnsNull() {
    BoundedEdge edge = new BoundedEdge();
    assertNull(edge.resolve(-1, 0, 5, 5));
  }

  @Test
  void boundedEdge_negativeColReturnsNull() {
    BoundedEdge edge = new BoundedEdge();
    assertNull(edge.resolve(0, -1, 5, 5));
  }

  @Test
  void boundedEdge_rowAtLimitReturnsNull() {
    BoundedEdge edge = new BoundedEdge();
    assertNull(edge.resolve(5, 0, 5, 5));
  }

  @Test
  void boundedEdge_colAtLimitReturnsNull() {
    BoundedEdge edge = new BoundedEdge();
    assertNull(edge.resolve(0, 5, 5, 5));
  }

  @Test
  void boundedEdge_originReturnsSelf() {
    BoundedEdge edge = new BoundedEdge();
    assertArrayEquals(new int[]{0, 0}, edge.resolve(0, 0, 5, 5));
  }

  // ─── ToroidalEdge ─────────────────────────────────────────────────────────────

  @Test
  void toroidalEdge_inBoundsReturnsCell() {
    ToroidalEdge edge = new ToroidalEdge();
    assertArrayEquals(new int[]{2, 3}, edge.resolve(2, 3, 5, 5));
  }

  @Test
  void toroidalEdge_negativeRowWrapsToBottom() {
    ToroidalEdge edge = new ToroidalEdge();
    // row -1 in a 5-row grid → row 4
    assertArrayEquals(new int[]{4, 0}, edge.resolve(-1, 0, 5, 5));
  }

  @Test
  void toroidalEdge_negativeColWrapsToRight() {
    ToroidalEdge edge = new ToroidalEdge();
    // col -1 in a 5-col grid → col 4
    assertArrayEquals(new int[]{0, 4}, edge.resolve(0, -1, 5, 5));
  }

  @Test
  void toroidalEdge_rowPastEndWrapsToTop() {
    ToroidalEdge edge = new ToroidalEdge();
    // row 5 in a 5-row grid → row 0
    assertArrayEquals(new int[]{0, 0}, edge.resolve(5, 0, 5, 5));
  }

  @Test
  void toroidalEdge_colPastEndWrapsToLeft() {
    ToroidalEdge edge = new ToroidalEdge();
    // col 5 in a 5-col grid → col 0
    assertArrayEquals(new int[]{0, 0}, edge.resolve(0, 5, 5, 5));
  }

  @Test
  void toroidalEdge_cornerWrapsCorrectly() {
    ToroidalEdge edge = new ToroidalEdge();
    // (-1, -1) in 4x4 → (3, 3)
    assertArrayEquals(new int[]{3, 3}, edge.resolve(-1, -1, 4, 4));
  }

  // ─── MirrorEdge ─────────────────────────────────────────────────────────────

  @Test
  void mirrorEdge_inBoundsReturnsCell() {
    MirrorEdge edge = new MirrorEdge();
    assertArrayEquals(new int[]{2, 3}, edge.resolve(2, 3, 5, 5));
  }

  @Test
  void mirrorEdge_negativeRowClampsToZero() {
    MirrorEdge edge = new MirrorEdge();
    assertArrayEquals(new int[]{0, 2}, edge.resolve(-1, 2, 5, 5));
  }

  @Test
  void mirrorEdge_negativeColClampsToZero() {
    MirrorEdge edge = new MirrorEdge();
    assertArrayEquals(new int[]{2, 0}, edge.resolve(2, -3, 5, 5));
  }

  @Test
  void mirrorEdge_rowPastEndClampsToLastRow() {
    MirrorEdge edge = new MirrorEdge();
    // row 7 in a 5-row grid → row 4
    assertArrayEquals(new int[]{4, 0}, edge.resolve(7, 0, 5, 5));
  }

  @Test
  void mirrorEdge_colPastEndClampsToLastCol() {
    MirrorEdge edge = new MirrorEdge();
    // col 10 in a 5-col grid → col 4
    assertArrayEquals(new int[]{0, 4}, edge.resolve(0, 10, 5, 5));
  }

  @Test
  void mirrorEdge_cornerClampsToCorner() {
    MirrorEdge edge = new MirrorEdge();
    // (-2, -2) in 4x4 → (0, 0)
    assertArrayEquals(new int[]{0, 0}, edge.resolve(-2, -2, 4, 4));
  }
}
