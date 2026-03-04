/**
 * Controller API that coordinates the model and view layers.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li>Decouple the model ({@link cellsociety.model.Game}) from the view
 *       ({@link cellsociety.view.grids.GridView}) so that neither layer depends
 *       on the other directly. {@link cellsociety.model.ParseResult} is the sole
 *       object that crosses both boundaries: it flows model → controller → view
 *       and back on save/edit.</li>
 *   <li>Manage the JavaFX animation timeline and all user-driven playback
 *       transitions (play, pause, step, reset) in one place so that view classes
 *       contain no timing logic.</li>
 *   <li>Orchestrate the full application lifecycle — start screen, file loading,
 *       game construction, and multi-window support — through
 *       {@link cellsociety.controller.GameRunner}. To add a new user action,
 *       supply a {@link java.lang.Runnable} callback to the relevant screen
 *       constructor; do not add methods to the screen classes themselves.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li>{@link cellsociety.controller.Controller} is the sole class that drives
 *       {@link cellsociety.model.Game#step()} and pushes state integers to the
 *       {@link cellsociety.view.grids.GridView}; view classes must not call
 *       model methods directly.</li>
 *   <li>{@link cellsociety.controller.PropertiesLoader} is the single entry point
 *       for resource bundle loading; all callers must use it rather than calling
 *       {@link java.util.ResourceBundle#getBundle} directly.</li>
 *   <li>XML-specific checked exceptions are wrapped inside the model layer
 *       ({@code XMLWriter}, {@code XMLReader}) before reaching this package;
 *       callers here catch {@link java.lang.IllegalStateException} or
 *       {@link java.lang.IllegalArgumentException} only.</li>
 * </ul>
 */
package cellsociety.controller;
