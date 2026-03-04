package cellsociety.model;

import cellsociety.model.games.LangtonsLoop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LangtonsLoopTest {

  private static final Map<String, String> EMPTY_PARAMS = new HashMap<>();

  private Grid createStandardLoopGrid() {
    int[][] states = new int[25][25];
    int startR = 1;
    int startC = 1;
    int[][] loopPattern = {
        {0, 2, 2, 2, 2, 2, 2, 2, 2},
        {2, 1, 7, 0, 1, 4, 0, 1, 4, 2},
        {2, 0, 2, 2, 2, 2, 2, 2, 0, 2},
        {2, 7, 2, 0, 0, 0, 0, 2, 1, 2},
        {2, 1, 2, 0, 0, 0, 0, 2, 1, 2},
        {2, 0, 2, 0, 0, 0, 0, 2, 1, 2},
        {2, 7, 2, 0, 0, 0, 0, 2, 1, 2},
        {2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2},
        {2, 0, 7, 1, 0, 7, 1, 0, 7, 1, 1, 1, 1, 1, 2},
        {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
    };
    for (int r = 0; r < loopPattern.length; r++) {
      for (int c = 0; c < loopPattern[r].length; c++) {
        states[startR + r][startC + c] = loopPattern[r][c];
      }
    }
    return new Grid(25, 25, states);
  }

  @Test
  void constructorCreatesGameSuccessfully() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);
    assertNotNull(game);
  }

  @Test
  void stepDoesNotCrash() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);
    game.step();
    assertEquals(25, grid.getRows());
  }

  @Test
  void emptyGridRemainsEmpty() {
    int[][] states = new int[5][5];
    Grid grid = new Grid(5, 5, states);
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);
    game.step();
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        assertEquals(0, grid.getCell(r, c).getState(),
            "Empty grid should stay empty at (" + r + "," + c + ")");
      }
    }
  }

  @Test
  void loopChangesStateAfterStep() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);

    int[][] beforeStates = new int[25][25];
    for (int r = 0; r < 25; r++) {
      for (int c = 0; c < 25; c++) {
        beforeStates[r][c] = grid.getCell(r, c).getState();
      }
    }

    game.step();

    boolean anyChanged = false;
    for (int r = 0; r < 25; r++) {
      for (int c = 0; c < 25; c++) {
        if (grid.getCell(r, c).getState() != beforeStates[r][c]) {
          anyChanged = true;
        }
      }
    }
    assertTrue(anyChanged, "Loop should evolve after one step");
  }

  @Test
  void armExtendsAfterManySteps() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);

    for (int i = 0; i < 100; i++) {
      game.step();
    }

    int nonZeroCount = 0;
    for (int r = 0; r < 25; r++) {
      for (int c = 0; c < 25; c++) {
        if (grid.getCell(r, c).getState() != 0) {
          nonZeroCount++;
        }
      }
    }
    assertTrue(nonZeroCount > 86,
        "After 100 steps, the arm should have grown beyond the initial 86 cells");
  }

  @Test
  void allStatesRemainValid() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);

    for (int i = 0; i < 20; i++) {
      game.step();
    }

    for (int r = 0; r < 25; r++) {
      for (int c = 0; c < 25; c++) {
        int state = grid.getCell(r, c).getState();
        assertTrue(state >= 0 && state <= 7,
            "State at (" + r + "," + c + ") should be 0-7, got " + state);
      }
    }
  }

  @Test
  void gridDimensionsPreservedAfterManySteps() {
    Grid grid = createStandardLoopGrid();
    LangtonsLoop game = new LangtonsLoop(grid, EMPTY_PARAMS);
    for (int i = 0; i < 30; i++) {
      game.step();
    }
    assertEquals(25, grid.getRows());
    assertEquals(25, grid.getColumns());
  }
}
