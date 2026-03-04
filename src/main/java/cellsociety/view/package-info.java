/**
 * View API for the Cell Society cellular automaton simulator.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li>Render simulation state and provide user controls without depending on
 *       concrete model classes. {@link cellsociety.model.ParseResult} is the
 *       <em>only</em> model object that crosses into this package; all other
 *       model data arrives through {@link java.lang.Runnable} callbacks wired
 *       by the controller.</li>
 *   <li>Support multiple grid shapes through the
 *       {@link cellsociety.view.grids.GridView} abstraction; the controller and
 *       factory depend only on that abstract type, never on a specific subclass.</li>
 *   <li>Support localisation and theming without changes to individual screens:
 *       to add a new screen, extend {@link cellsociety.view.StylableView} and
 *       call {@code registerThemeListenerWithLayoutSheet} and
 *       {@code registerLanguageListener} in the constructor.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li>The view never calls model mutation methods directly; it only invokes
 *       {@link java.lang.Runnable} callbacks supplied by the controller.</li>
 *   <li>{@link cellsociety.view.ErrorView} must be used for all user-facing error
 *       reporting; {@code System.err} output and raw stack traces are forbidden.</li>
 *   <li>Stylesheets must be applied through {@link cellsociety.view.StylableView}'s
 *       registration methods so theme changes propagate to every open window
 *       consistently — screens must not add stylesheets manually.</li>
 * </ul>
 */
package cellsociety.view;
