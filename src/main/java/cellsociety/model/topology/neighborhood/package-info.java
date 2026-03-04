/**
 * Neighborhood strategy API for determining which cells are neighbors.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li>Allow the neighbor definition to vary independently of the grid and
 *       simulation rules via the Strategy pattern. To add a new neighborhood
 *       type, implement {@link cellsociety.model.topology.neighborhood.NeighborhoodStrategy}
 *       and return a {@code List<int[]>} of {@code {rowOffset, colOffset}} pairs;
 *       register the class name in the XML config's {@code <neighborhood>} element.</li>
 *   <li>Support position-dependent neighborhoods (e.g., hexagonal offset grids,
 *       triangular grids where up-pointing and down-pointing cells differ) by
 *       passing the cell's own coordinates to {@code getOffsets(int row, int col)}.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li>Implementations must be stateless and return the same offsets for the
 *       same (row, col) inputs on every call — no mutable instance state.</li>
 *   <li>Offsets are relative to the queried cell; out-of-bounds resolution is
 *       handled separately by {@link cellsociety.model.topology.edgebehavior.EdgePolicy}
 *       — implementations must not clamp or wrap coordinates themselves.</li>
 *   <li>The center offset {@code {0, 0}} must never be included — a cell is not
 *       its own neighbor.</li>
 * </ul>
 */
package cellsociety.model.topology.neighborhood;
