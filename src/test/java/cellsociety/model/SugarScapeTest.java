package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.model.games.SugarScape;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SugarScapeTest {

  private Map<String, String> defaultParams() {
    Map<String, String> params = new HashMap<>();
    params.put("numAgents", "5");
    params.put("minVision", "1");
    params.put("maxVision", "3"); 
    params.put("minMetabolism", "1");
    params.put("maxMetabolism", "2");
    params.put("minInitSugar", "5");
    params.put("maxInitSugar", "10");
    params.put("sugarGrowBackRate", "1");
    params.put("sugarGrowBackInterval", "1");
    return params;
  }

  private Grid createSugarGrid() {
    int[][] states = {
        {0, 0, 0, 0, 0},
        {0, 1, 2, 1, 0},
        {0, 2, 4, 2, 0},
        {0, 1, 2, 1, 0},
        {0, 0, 0, 0, 0}
    };
    return new Grid(5, 5, states);
  }

  @Test
  void constructorInitializesWithoutError() {
    Grid grid = createSugarGrid();
    new SugarScape(grid, defaultParams());
    assertEquals(5, grid.getRows());
  }

  @Test
  void stepDoesNotCrash() {
    Grid grid = createSugarGrid();
    SugarScape game = new SugarScape(grid, defaultParams());
    game.step();
    assertTrue(grid.getRows() > 0);
  }

  @Test
  void agentsConsumeResourcesAfterStep() {
    int[][] states = {
        {4, 4, 4},
        {4, 4, 4},
        {4, 4, 4}
    };
    Grid grid = new Grid(3, 3, states);
    Map<String, String> params = defaultParams();
    params.put("numAgents", "9");
    params.put("minVision", "1");
    params.put("maxVision", "1");
    SugarScape game = new SugarScape(grid, params);
    game.step();

    boolean anyConsumed = false;
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        int state = grid.getCell(r, c).getState();
        if (state != 4) {
          anyConsumed = true;
        }
      }
    }
    assertTrue(anyConsumed, "Agents should have consumed some sugar");
  }

  @Test
  void agentDisplayStateIsFive() {
    int[][] states = {
        {4, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };
    Grid grid = new Grid(3, 3, states);
    Map<String, String> params = defaultParams();
    params.put("numAgents", "1");
    SugarScape game = new SugarScape(grid, params);
    game.step();

    boolean foundAgent = false;
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        if (grid.getCell(r, c).getState() == 5) {
          foundAgent = true;
        }
      }
    }
    assertTrue(foundAgent, "At least one cell should show agent display state (5)");
  }

  @Test
  void zeroAgentsProducesEmptySimulation() {
    Grid grid = createSugarGrid();
    Map<String, String> params = defaultParams();
    params.put("numAgents", "0");
    SugarScape game = new SugarScape(grid, params);
    game.step();

    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertTrue(grid.getCell(r, c).getState() <= 4,
            "No agents means no state 5");
      }
    }
  }

  @Test
  void multipleStepsReducePopulationOnBarrenGrid() {
    int[][] states = new int[5][5];
    Grid grid = new Grid(5, 5, states);
    Map<String, String> params = defaultParams();
    params.put("numAgents", "10");
    params.put("minInitSugar", "1");
    params.put("maxInitSugar", "2");
    params.put("minMetabolism", "3");
    params.put("maxMetabolism", "4");
    SugarScape game = new SugarScape(grid, params);

    for (int i = 0; i < 20; i++) {
      game.step();
    }

    int agentCount = 0;
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        if (grid.getCell(r, c).getState() == 5) {
          agentCount++;
        }
      }
    }
    assertTrue(agentCount < 10,
        "Agents on a barren grid with high metabolism should die off");
  }

  @Test
  void gridDimensionsPreservedAfterStep() {
    Grid grid = createSugarGrid();
    SugarScape game = new SugarScape(grid, defaultParams());
    game.step();
    assertEquals(5, grid.getRows());
    assertEquals(5, grid.getColumns());
  }

  @Test
  void growBackIntervalDelaysRegrowth() {
    int[][] states = {
        {4, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };
    Grid grid = new Grid(3, 3, states);
    Map<String, String> params = defaultParams();
    params.put("numAgents", "0");
    params.put("sugarGrowBackRate", "1");
    params.put("sugarGrowBackInterval", "3");
    SugarScape game = new SugarScape(grid, params);

    game.step();
    int sugarAfterTick1 = grid.getCell(0, 0).getState();

    game.step();
    int sugarAfterTick2 = grid.getCell(0, 0).getState();

    assertEquals(sugarAfterTick1, sugarAfterTick2,
        "Sugar should not grow until interval is reached");
  }
}
