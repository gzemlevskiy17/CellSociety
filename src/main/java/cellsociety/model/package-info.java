/**
 * Core model API for the Cell Society cellular automaton simulator.
 *
 * <h2>Design Goals</h2>
 * <ul>
 * <li>Represent grid state and simulation rules independently of any UI
 * framework.</li>
 * <li>Support arbitrary simulation types via the {@link cellsociety.model.Game}
 * abstract class
 * without requiring changes to the factory or controller: to add a new
 * simulation,
 * subclass {@code Game}, implement {@code updateGame()}, and name the class in
 * an
 * XML {@code <type>} element — no other files need to change.</li>
 * <li>Expose a minimal read-only surface ({@link cellsociety.model.GridReader})
 * to
 * the controller layer so the internal grid structure is never leaked.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 * <li>{@link cellsociety.model.Game#step()} always performs a two-phase update:
 * compute next states, then commit them simultaneously — subclasses must call
 * {@code setNextState} during {@code updateGame()} and never mutate cells
 * in-place.</li>
 * <li>{@link cellsociety.model.ParseResult} is the sole data-transfer object
 * crossing
 * the model → controller boundary. It is an immutable data container:
 * collection
 * getters return unmodifiable views and {@code getCells()} returns a defensive
 * copy.</li>
 * <li>{@link cellsociety.model.XMLReader} throws
 * {@link java.lang.IllegalArgumentException}
 * for any invalid or malformed configuration file; callers must not catch
 * {@code NullPointerException} as a substitute for validation.</li>
 * </ul>
 */
package cellsociety.model;
