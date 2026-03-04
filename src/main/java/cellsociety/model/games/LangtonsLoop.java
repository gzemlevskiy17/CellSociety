package cellsociety.model.games;

import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.HashMap;
import java.util.Map;

/**
 * Langton's Loop: a self-replicating cellular automaton.
 *
 * <p>
 * Uses 8 states and the von Neumann neighborhood with rotational symmetry.
 * A loop of cells containing genetic information flows continuously around
 * the loop and out along a pseudopod, which becomes the daughter loop.
 * The genes instruct it to make three left turns, completing the loop,
 * which then disconnects from its parent.
 * </p>
 *
 * <p>
 * States:
 * <ul>
 *   <li>0 - Background (empty)</li>
 *   <li>1 - Core (conducts signals)</li>
 *   <li>2 - Sheath (boundary)</li>
 *   <li>3 - Left turning / bonding / capping</li>
 *   <li>4 - Controls turning and finishing</li>
 *   <li>5 - Disconnects parent from offspring</li>
 *   <li>6 - Guides sprout growth</li>
 *   <li>7 - Straight growth information</li>
 * </ul>
 * </p>
 *
 * <p>
 * Subclasses (e.g., {@link SDSRLoop}) can extend the transition table
 * and override {@link #resolveUndefined} for additional behavior.
 * </p>
 *
 * @author Michael Li
 * @see Game
 * @see SDSRLoop
 */
public class LangtonsLoop extends Game {

  private static final int PACK_BASE = 9;
  private static final int BACKGROUND = 0;

  private final Map<Integer, Integer> transitions;

  private static final String[] LANGTON_RULES = {
      "000000", "000012", "000020", "000030", "000050", "000063",
      "000071", "000112", "000122", "000132", "000212", "000220",
      "000230", "000262", "000272", "000320", "000525", "000622",
      "000722", "001022", "001120", "002020", "002030", "002050",
      "002125", "002220", "002322", "005222", "012321", "012421",
      "012525", "012621", "012721", "012751", "014221", "014321",
      "014421", "014721", "016251", "017221", "017255", "017521",
      "017621", "017721", "025271", "100011", "100061", "100077",
      "100111", "100121", "100211", "100244", "100277", "100511",
      "101011", "101111", "101244", "101277", "102026", "102121",
      "102211", "102244", "102263", "102277", "102327", "102424",
      "102626", "102644", "102677", "102710", "102727", "105427",
      "111121", "111221", "111244", "111251", "111261", "111277",
      "111522", "112121", "112221", "112244", "112251", "112277",
      "112321", "112424", "112621", "112727", "113221", "122244",
      "122277", "122434", "122547", "123244", "123277", "124255",
      "124267", "125275", "200012", "200022", "200042", "200071",
      "200122", "200152", "200212", "200222", "200232", "200242",
      "200250", "200262", "200272", "200326", "200423", "200517",
      "200522", "200575", "200722", "201022", "201122", "201222",
      "201422", "201722", "202022", "202032", "202052", "202073",
      "202122", "202152", "202212", "202222", "202272", "202321",
      "202422", "202452", "202520", "202552", "202622", "202722",
      "203122", "203216", "203226", "203422", "204222", "205122",
      "205212", "205222", "205521", "205725", "206222", "206722",
      "207122", "207222", "207422", "207722", "211222", "211261",
      "212222", "212242", "212262", "212272", "214222", "215222",
      "216222", "217222", "222272", "222442", "222462", "222762",
      "222772", "300013", "300022", "300041", "300076", "300123",
      "300421", "300622", "301021", "301220", "302511", "401120",
      "401220", "401250", "402120", "402221", "402326", "402520",
      "403221", "500022", "500215", "500225", "500232", "500272",
      "500520", "502022", "502122", "502152", "502220", "502244",
      "502722", "512122", "512220", "512422", "512722", "600011",
      "600021", "602120", "612125", "612131", "612225", "700077",
      "701120", "701220", "701250", "702120", "702221", "702251",
      "702321", "702525", "702720"
  };

  /**
   * Constructs a Langton's Loop simulation.
   *
   * @param grid   the grid with initial cell states (0-7)
   * @param params simulation parameters (unused for standard Langton's Loop)
   */
  public LangtonsLoop(Grid grid, Map<String, String> params) {
    super(grid, params);
    transitions = new HashMap<>();
    loadRules(LANGTON_RULES);
  }

  /**
   * Constructs a loop simulation with a pre-built transition table.
   * Used by subclasses that extend the Langton rule set.
   *
   * @param grid        the grid
   * @param params      simulation parameters
   * @param transitions pre-built transition map
   */
  protected LangtonsLoop(Grid grid, Map<String, String> params,
      Map<Integer, Integer> transitions) {
    super(grid, params);
    this.transitions = transitions;
  }

  @Override
  protected void updateGame() {
    Grid grid = getGrid();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getColumns(); c++) {
        int current = grid.getCell(r, c).getState();
        int north = getStateAt(r - 1, c);
        int east = getStateAt(r, c + 1);
        int south = getStateAt(r + 1, c);
        int west = getStateAt(r, c - 1);

        int key = packKey(current, north, east, south, west);
        Integer newState = transitions.get(key);
        if (newState == null) {
          newState = resolveUndefined(current, north, east, south, west);
        }
        grid.getCell(r, c).setNextState(newState);
      }
    }
  }

  /**
   * Handles configurations not found in the transition table.
   * Standard Langton's Loop keeps the current state unchanged.
   * Subclasses override for additional behavior (e.g., dissolution).
   */
  protected int resolveUndefined(int current, int north, int east, int south, int west) {
    return current;
  }

  protected int getStateAt(int row, int col) {
    int[] resolved = getGrid().getEdgePolicy().resolve(
        row, col, getGrid().getRows(), getGrid().getColumns());
    if (resolved == null) {
      return BACKGROUND;
    }
    return getGrid().getCell(resolved[0], resolved[1]).getState();
  }

  protected void loadRules(String[] rules) {
    for (String rule : rules) {
      int c = rule.charAt(0) - '0';
      int n = rule.charAt(1) - '0';
      int e = rule.charAt(2) - '0';
      int s = rule.charAt(3) - '0';
      int w = rule.charAt(4) - '0';
      int result = rule.charAt(5) - '0';
      addRule(c, n, e, s, w, result);
      addRule(c, e, s, w, n, result);
      addRule(c, s, w, n, e, result);
      addRule(c, w, n, e, s, result);
    }
  }

  protected void addRule(int c, int n, int e, int s, int w, int result) {
    transitions.put(packKey(c, n, e, s, w), result);
  }

  protected static int packKey(int c, int n, int e, int s, int w) {
    return (((c * PACK_BASE + n) * PACK_BASE + e) * PACK_BASE + s) * PACK_BASE + w;
  }
}
