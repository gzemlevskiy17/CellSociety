/**
 * Edge policy API for resolving out-of-bounds neighbor positions.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li>Allow the grid boundary behavior to vary independently of the simulation
 *       rules and neighborhood definition via the Strategy pattern. To add a new
 *       edge policy, implement {@link cellsociety.model.topology.edgebehavior.EdgePolicy}
 *       with the single method {@code int[] resolve(int row, int col, int rows, int cols)};
 *       register the class name in the XML config's {@code <edge>} element.</li>
 *   <li>Keep the {@link cellsociety.model.Grid} neighbor-lookup loop simple: it
 *       always delegates boundary resolution to the active policy without
 *       conditional branching — the policy is the only place boundary logic lives.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li>{@code resolve} returns {@code null} to signal that the neighbor should
 *       be skipped (e.g., the bounded/finite edge), or a valid
 *       {@code int[]{row, col}} array otherwise.</li>
 *   <li>Implementations must be stateless and pure — the same inputs always
 *       produce the same output; no mutable instance state is permitted.</li>
 *   <li>When a non-null array is returned, both coordinates must satisfy
 *       {@code 0 <= row < rows} and {@code 0 <= col < cols}.</li>
 * </ul>
 */
package cellsociety.model.topology.edgebehavior;
