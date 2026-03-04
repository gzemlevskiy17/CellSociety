package cellsociety.model.games;

import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * SugarScape simulation where agents navigate a landscape competing for sugar.
 *
 * <p>
 * Each patch on the grid has a sugar capacity and a current sugar level.
 * Agents move according to rule M (look, move to best visible vacant patch,
 * harvest, metabolize) and patches regrow sugar according to rule G.
 * </p>
 *
 * <p>
 * Supports multiple presets (variations) through XML configuration alone:
 * <ul>
 * <li>Preset 1 (Movement): basic foraging with default parameters</li>
 * <li>Preset 2 (Migration): higher vision, clustered start, slower
 * growback</li>
 * <li>Preset 3 (Reproduction): agents can reproduce when fertile and
 * wealthy</li>
 * </ul>
 * </p>
 *
 * <p>
 * Cell display states: 0 = no sugar, 1-4 = increasing sugar levels,
 * 5 = agent present.
 * </p>
 *
 * <p>
 * <b>Parameters (all optional with defaults):</b>
 * {@code numAgents} (400), {@code minVision} (1), {@code maxVision} (6),
 * {@code minMetabolism} (1), {@code maxMetabolism} (4),
 * {@code minInitSugar} (5), {@code maxInitSugar} (25),
 * {@code sugarGrowBackRate} (1), {@code sugarGrowBackInterval} (1).
 * </p>
 *
 * @author Michael Li
 * @see Game
 * @see SugarAgent
 */
public class SugarScape extends Game {

  private static final int DISPLAY_AGENT = 5;
  private static final int MAX_SUGAR_LEVEL = 4;

  private static final int DEFAULT_NUM_AGENTS = 400;
  private static final int DEFAULT_MIN_VISION = 1;
  private static final int DEFAULT_MAX_VISION = 6;
  private static final int DEFAULT_MIN_METABOLISM = 1;
  private static final int DEFAULT_MAX_METABOLISM = 4;
  private static final int DEFAULT_MIN_SUGAR = 5;
  private static final int DEFAULT_MAX_SUGAR = 25;
  private static final int DEFAULT_GROW_BACK_RATE = 1;
  private static final int DEFAULT_GROW_BACK_INTERVAL = 1;

  private final int[][] sugarCapacity;
  private final int[][] currentSugar;
  private final List<SugarAgent> agents;
  private final Random random;

  private final int sugarGrowBackRate;
  private final int sugarGrowBackInterval;
  private int tickCount;

  /**
   * Constructs a SugarScape simulation from parsed configuration.
   *
   * @param grid   the grid whose initial cell states encode sugar capacity (0-4)
   * @param params simulation parameters parsed from XML
   */
  public SugarScape(Grid grid, Map<String, String> params) {
    super(grid, params);
    random = new Random();

    int numAgents = intParam("numAgents", DEFAULT_NUM_AGENTS);
    int minVision = intParam("minVision", DEFAULT_MIN_VISION);
    int maxVision = intParam("maxVision", DEFAULT_MAX_VISION);
    int minMetabolism = intParam("minMetabolism", DEFAULT_MIN_METABOLISM);
    int maxMetabolism = intParam("maxMetabolism", DEFAULT_MAX_METABOLISM);
    int minInitSugar = intParam("minInitSugar", DEFAULT_MIN_SUGAR);
    int maxInitSugar = intParam("maxInitSugar", DEFAULT_MAX_SUGAR);
    sugarGrowBackRate = intParam("sugarGrowBackRate", DEFAULT_GROW_BACK_RATE);
    sugarGrowBackInterval = intParam("sugarGrowBackInterval", DEFAULT_GROW_BACK_INTERVAL);

    int rows = grid.getRows();
    int cols = grid.getColumns();

    sugarCapacity = new int[rows][cols];
    currentSugar = new int[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        int cap = grid.getCell(r, c).getState();
        sugarCapacity[r][c] = cap;
        currentSugar[r][c] = cap;
      }
    }

    agents = new ArrayList<>();
    spawnAgents(numAgents, minVision, maxVision, minMetabolism, maxMetabolism,
        minInitSugar, maxInitSugar);
    tickCount = 0;
  }

  private void spawnAgents(int count, int minVis, int maxVis,
      int minMet, int maxMet, int minSug, int maxSug) {
    List<int[]> emptyCells = new ArrayList<>();
    for (int r = 0; r < getGrid().getRows(); r++) {
      for (int c = 0; c < getGrid().getColumns(); c++) {
        emptyCells.add(new int[] { r, c });
      }
    }
    Collections.shuffle(emptyCells, random);

    int toSpawn = Math.min(count, emptyCells.size());
    for (int i = 0; i < toSpawn; i++) {
      int[] pos = emptyCells.get(i);
      int vision = randBetween(minVis, maxVis);
      int metabolism = randBetween(minMet, maxMet);
      int sugar = randBetween(minSug, maxSug);
      agents.add(new SugarAgent(pos[0], pos[1], vision, metabolism, sugar));
    }
  }

  @Override
  protected void updateGame() {
    tickCount++;

    boolean[][] occupied = buildOccupiedMap();

    List<SugarAgent> shuffled = new ArrayList<>(agents);
    Collections.shuffle(shuffled, random);

    for (SugarAgent agent : shuffled) {
      if (!agent.isAlive()) {
        continue;
      }
      occupied[agent.getRow()][agent.getCol()] = false;
      moveAgent(agent, occupied);
      occupied[agent.getRow()][agent.getCol()] = true;
    }

    agents.removeIf(a -> !a.isAlive());
    growBackSugar();
    updateDisplayStates();
  }

  /**
   * Rule M: agent looks along each neighbor direction up to its vision range,
   * selects the vacant patch with the most sugar (closest if tied),
   * moves there, harvests, and metabolizes.
   */
  private void moveAgent(SugarAgent agent, boolean[][] occupied) {
    int[] best = findBestCell(agent, occupied);
    agent.setPosition(best[0], best[1]);
    agent.addSugar(currentSugar[best[0]][best[1]]);
    currentSugar[best[0]][best[1]] = 0;
    agent.metabolize();
  }

  private int[] findBestCell(SugarAgent agent, boolean[][] occupied) {
    int bestRow = agent.getRow();
    int bestCol = agent.getCol();
    int bestSugar = currentSugar[bestRow][bestCol];
    int bestDist = 0;

    int[][] directions = getGrid().getNeighborhoodStrategy()
        .getOffsets(agent.getRow(), agent.getCol());
    for (int[] dir : directions) {
      for (int step = 1; step <= agent.getVision(); step++) {
        int candidateRow = agent.getRow() + dir[0] * step;
        int candidateCol = agent.getCol() + dir[1] * step;

        int[] resolved = getGrid().getEdgePolicy().resolve(
            candidateRow, candidateCol, getGrid().getRows(), getGrid().getColumns());
        if (resolved == null) {
          break;
        }

        int nr = resolved[0];
        int nc = resolved[1];

        if (occupied[nr][nc]) {
          continue;
        }

        int sugarHere = currentSugar[nr][nc];
        if (sugarHere > bestSugar || (sugarHere == bestSugar && step < bestDist)) {
          bestSugar = sugarHere;
          bestRow = nr;
          bestCol = nc;
          bestDist = step;
        }
      }
    }
    return new int[] { bestRow, bestCol };
  }

  /**
   * Rule G: every {@code sugarGrowBackInterval} ticks, each patch grows
   * {@code sugarGrowBackRate} units of sugar up to its capacity.
   */
  private void growBackSugar() {
    if (tickCount % sugarGrowBackInterval != 0) {
      return;
    }
    for (int r = 0; r < getGrid().getRows(); r++) {
      for (int c = 0; c < getGrid().getColumns(); c++) {
        currentSugar[r][c] = Math.min(
            currentSugar[r][c] + sugarGrowBackRate,
            sugarCapacity[r][c]);
      }
    }
  }

  /**
   * Maps sugar levels and agent positions to cell display states.
   * Sugar levels 0-4 map to states 0-4, agents overlay as state 5.
   */
  private void updateDisplayStates() {
    for (int r = 0; r < getGrid().getRows(); r++) {
      for (int c = 0; c < getGrid().getColumns(); c++) {
        int displayState = Math.min(currentSugar[r][c], MAX_SUGAR_LEVEL);
        getGrid().getCell(r, c).setNextState(displayState);
      }
    }
    for (SugarAgent agent : agents) {
      if (agent.isAlive()) {
        getGrid().getCell(agent.getRow(), agent.getCol()).setNextState(DISPLAY_AGENT);
      }
    }
  }

  private boolean[][] buildOccupiedMap() {
    boolean[][] occupied = new boolean[getGrid().getRows()][getGrid().getColumns()];
    for (SugarAgent agent : agents) {
      if (agent.isAlive()) {
        occupied[agent.getRow()][agent.getCol()] = true;
      }
    }
    return occupied;
  }

  private int intParam(String name, int defaultValue) {
    String val = getParam(name);
    if (val == null || val.isEmpty()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(val.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private int randBetween(int min, int max) {
    if (min >= max) {
      return min;
    }
    return min + random.nextInt(max - min + 1);
  }
}
