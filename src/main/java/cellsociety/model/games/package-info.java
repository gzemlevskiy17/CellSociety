/**
 * Concrete {@link cellsociety.model.Game} implementations for each supported simulation.
 *
 * <p>Each class extends {@link cellsociety.model.Game} and provides a specific
 * {@code updateGame()} rule set. Games are instantiated reflectively by
 * {@link cellsociety.model.ModelFactory} using the simulation type name from the XML config.</p>
 *
 * <ul>
 *   <li>{@link cellsociety.model.games.GameOfLife} — Conway's Game of Life (configurable rulestring)</li>
 *   <li>{@link cellsociety.model.games.ForestFire} — Probabilistic fire spread</li>
 *   <li>{@link cellsociety.model.games.Segregation} — Schelling's segregation model</li>
 *   <li>{@link cellsociety.model.games.Percolation} — Water percolation through a grid</li>
 *   <li>{@link cellsociety.model.games.WaTor} — Predator–prey (Wa-Tor World)</li>
 *   <li>{@link cellsociety.model.games.FallingSand} — Particle physics simulation</li>
 * </ul>
 */
package cellsociety.model.games;
