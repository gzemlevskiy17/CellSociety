package cellsociety.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract base class for all cellular automata simulations.
 *
 * <p>
 * Defines the core simulation loop: each {@link #step()} call first computes
 * the next state
 * for every cell via {@link #updateGame()}, then commits all changes
 * simultaneously via
 * {@link #commitChanges()}. This two-phase approach ensures synchronous updates
 * so that the order
 * cells are processed does not affect the result.
 * </p>
 *
 * <p>
 * <b>Assumptions:</b> The provided {@link Grid} is non-null and fully
 * initialized with
 * valid {@link Cell} objects. Subclasses must implement {@link #updateGame()}
 * to set each cell's
 * next state without modifying the current state directly.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Grid}, {@link Cell}
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * Grid grid = new Grid(10, 10);
 * Game game = new GameOfLife(grid);
 * game.step(); // advances the simulation by one generation
 * }</pre>
 *
 * @author Michael Li
 * @author Georgiy Zemlevskiy
 */
public abstract class Game {

  private Grid grid;
  private Map<String, String> gameParams;

  /**
   * Constructs a Game with the given grid.
   *
   * @param grid the grid of cells for this simulation; must not be null
   */
  public Game(Grid grid) {
    this.grid = grid;
    this.gameParams = new LinkedHashMap<>();
  }

  /**
   * Constructs a Game with the given grid and known name of parameters.
   *
   * @param grid      the grid of cells for this simulation; must not be null
   * @param paramName array of parameter names that will be used to access
   *                  gameParams
   */
  public Game(Grid grid, Map<String, String> gameParams) {
    this.grid = grid;
    this.gameParams = gameParams;
  }

  /**
   * Advances the simulation by one step. Computes next states for all cells, then
   * commits the
   * changes simultaneously.
   */
  public void step() {
    grid.saveState();
    updateGame();
    commitChanges();
  }

  /**
   * Reverts the grid to the previously saved state. Sets the grid state to the
   * last state stored in
   * the grid's history.
   */
  public void stepBack() {
    grid.revertState(); // TODO: verify logic
  }

  /**
   * Sets the grid state to the initial configuration captured when the grid was
   * created.
   */
  public void reset() {
    grid.resetToInitialState();
  } // TODO: test

  /**
   * Computes the next state for every cell based on the simulation's rules.
   * Implementations should
   * call {@link Cell#setNextState(int)} and must not call
   * {@link Cell#updateState()} directly.
   */
  protected abstract void updateGame();

  /**
   * Commits all pending next-state values to the current state for every cell in
   * the grid. Called
   * after {@link #updateGame()} to ensure synchronous updates.
   */
  protected void commitChanges() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumns(); j++) {
        grid.getCell(i, j).updateState();
      }
    }
  }

  /**
   * Returns the grid backing this simulation.
   *
   * @return the simulation's {@link Grid}
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Returns a read-only {@link GridReader} for the grid backing this simulation.
   * Controller-layer code should use this instead of {@link #getGrid()} to avoid
   * exposing the model's internal data structure.
   *
   * @return a {@link GridReader} wrapping this simulation's grid
   */
  public GridReader getGridReader() {
    return new GridReader(grid);
  }

  /**
   * Returns the cell at the specified row and column.
   *
   * @param row the row index (0-based)
   * @param col the column index (0-based)
   * @return the {@link Cell} at the given position
   */
  public Cell getCell(int row, int col) {
    return grid.getCell(row, col);
  }

  // TODO: does this work for all types of games?
  public void clearGrid() {
    grid.defaultInitializeCells();
  }

  public String getParam(String name) {
    if (gameParams == null)
      throw new NullPointerException("Parameter Storage does not exist");
    try {
      return gameParams.get(name);
    } catch (Exception e) {
      return "";
    }
  }

  public void setParam(String name, String newValue) {
    gameParams.put(name, newValue);
  }

  public void updateParams(Map<String, String> gameParams) {
    this.gameParams.putAll(gameParams);
  }
}
