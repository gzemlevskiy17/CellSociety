package cellsociety.model;

/**
 * Represents a single cell in the simulation grid.
 *
 * <p>Each cell holds a current state and a pending next state. This two-field design supports
 * synchronous updates: during a simulation step, {@link Game#updateGame()} sets each cell's
 * next state via {@link #setNextState(int)}, then {@link Game#commitChanges()} calls
 * {@link #updateState()} to apply them all at once.</p>
 *
 * <p><b>Assumptions:</b> State values are non-negative integers (e.g., 0 = dead/empty,
 * 1 = alive, 2+ = other types). The caller is responsible for ensuring valid state values.</p>
 *
 * <p><b>Dependencies:</b> None — this is a standalone data class used by {@link Grid} and
 * {@link Game}.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * Cell cell = new Cell(0);
 * cell.setNextState(1);
 * cell.updateState();
 * cell.getState(); // returns 1
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 */
public class Cell {
  private int state;
  private int nextState;

  /**
   * Constructs a cell with the given initial state. Both current and next state
   * are set to this value.
   *
   * @param state the initial state of the cell
   */
  public Cell(int state) {
    this.state = state;
    this.nextState = state;
  }

  /**
   * Immediately sets both the current and next state. Use this for initial grid
   * setup, not during simulation steps.
   *
   * @param state the new state to assign
   */
  public void setState(int state) {
    this.state = state;
    this.nextState = state;
  }

  /**
   * Commits the pending next state to the current state. Called during
   * {@link Game#commitChanges()} after all cells have computed their next state.
   */
  public void updateState() {
    this.state = this.nextState;
  }

  /**
   * Sets the pending next state without changing the current state. Used during
   * {@link Game#updateGame()} to stage the next generation.
   *
   * @param state the next state to stage
   */
  public void setNextState(int state) {
    this.nextState = state;
  }

  /**
   * Returns the pending next state.
   *
   * @return the next state value
   */
  public int getNextState() {
    return nextState;
  }

  /**
   * Returns the current state.
   *
   * @return the current state value
   */
  public int getState() {
    return state;
  }

  /**
   * Returns a new {@link Cell} with the same current and next state as this cell.
   * Used by {@link Grid} to snapshot the grid for history and reset purposes.
   *
   * @return a deep copy of this cell
   */
  public Cell copy() {
    Cell copy = new Cell(this.state);
    copy.setNextState(this.nextState);
    return copy;
  }
}
