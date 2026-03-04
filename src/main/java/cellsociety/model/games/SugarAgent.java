package cellsociety.model.games;

/**
 * An agent in the SugarScape simulation.
 *
 * <p>
 * Each agent has a position on the grid, genetic traits (vision and metabolism
 * that never change after birth), and a mutable sugar reserve. An agent dies
 * when its sugar drops to zero or below.
 * </p>
 *
 * @author Michael Li
 * @see SugarScape
 */
public class SugarAgent {

  private int row;
  private int col;
  private final int vision;
  private final int sugarMetabolism;
  private int sugar;
  private boolean alive;

  /**
   * Constructs a new SugarAgent at the given position.
   *
   * @param row             starting row
   * @param col             starting column
   * @param vision          how far the agent can see (fixed at birth)
   * @param sugarMetabolism sugar consumed per tick (fixed at birth)
   * @param initialSugar    starting sugar endowment
   */
  public SugarAgent(int row, int col, int vision, int sugarMetabolism, int initialSugar) {
    this.row = row;
    this.col = col;
    this.vision = vision;
    this.sugarMetabolism = sugarMetabolism;
    this.sugar = initialSugar;
    this.alive = true;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public int getVision() {
    return vision;
  }

  public int getSugarMetabolism() {
    return sugarMetabolism;
  }

  public int getSugar() {
    return sugar;
  }

  public void addSugar(int amount) {
    this.sugar += amount;
  }

  public void metabolize() {
    this.sugar -= sugarMetabolism;
    if (this.sugar <= 0) {
      this.alive = false;
    }
  }

  public boolean isAlive() {
    return alive;
  }
}
