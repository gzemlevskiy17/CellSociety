/**
 * Grid rendering API for the Cell Society view layer.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li>Provide a single abstract type, {@link cellsociety.view.grids.GridView},
 *       that the controller and factory can depend on regardless of the underlying
 *       cell shape (rectangle, hexagon, triangle). The controller communicates
 *       only through state {@code int} values and a
 *       {@code Map<Integer, Color>} color map — no shape-specific types are
 *       ever exposed upward.</li>
 *   <li>Confine shape-specific rendering logic to concrete subclasses so that
 *       adding a new grid shape requires only: (1) a new subclass that overrides
 *       {@code initializeGridPane()}, {@code updateCell()}, {@code addMouseEvents()},
 *       and {@code toggleGridOutline()}; and (2) an entry in
 *       {@link cellsociety.view.ViewFactory} — no changes to the controller.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li>Subclasses must implement {@link cellsociety.view.grids.GridView#initializeGridPane()},
 *       {@link cellsociety.view.grids.GridView#updateCell(int, int, int)},
 *       {@link cellsociety.view.grids.GridView#addMouseEvents(cellsociety.controller.Controller)},
 *       and {@link cellsociety.view.grids.GridView#toggleGridOutline()}.</li>
 *   <li>{@code updateCell} must only change the visual fill of the indicated cell;
 *       it must not alter grid dimensions or re-initialize the pane.</li>
 *   <li>All constants controlling maximum display size are private to
 *       {@link cellsociety.view.grids.GridView} and accessed by subclasses through
 *       protected getter methods only — no protected fields are permitted.</li>
 * </ul>
 */
package cellsociety.view.grids;
