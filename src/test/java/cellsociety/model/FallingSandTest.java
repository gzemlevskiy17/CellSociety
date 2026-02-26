package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.games.FallingSand;
import org.junit.jupiter.api.Test;

class FallingSandTest {

  @Test
  void testSandFallsIntoEmpty() {
    Grid grid = new Grid(3, 1);
    grid.getCell(0, 0).setState(FallingSand.SAND);
    // row 1 and 2 are EMPTY

    FallingSand game = new FallingSand(grid);
    // Run many steps so the single sand grain definitely moves down
    for (int i = 0; i < 100; i++) {
      game.step();
    }

    // Sand should have fallen to bottom row
    assertEquals(FallingSand.SAND, grid.getCell(2, 0).getState());
    assertEquals(FallingSand.EMPTY, grid.getCell(0, 0).getState());
  }

  @Test
  void testMetalNeverMoves() {
    Grid grid = new Grid(3, 3);
    grid.getCell(0, 0).setState(FallingSand.METAL);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 100; i++) {
      game.step();
    }

    assertEquals(FallingSand.METAL, grid.getCell(0, 0).getState());
  }

  @Test
  void testSandStopsOnMetal() {
    Grid grid = new Grid(3, 1);
    grid.getCell(0, 0).setState(FallingSand.SAND);
    grid.getCell(2, 0).setState(FallingSand.METAL);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 100; i++) {
      game.step();
    }

    // Sand should rest on top of metal (row 1), metal stays at row 2
    assertEquals(FallingSand.SAND, grid.getCell(1, 0).getState());
    assertEquals(FallingSand.METAL, grid.getCell(2, 0).getState());
  }

  @Test
  void testSandSinksThroughWater() {
    Grid grid = new Grid(3, 1);
    grid.getCell(0, 0).setState(FallingSand.SAND);
    grid.getCell(1, 0).setState(FallingSand.WATER);
    grid.getCell(2, 0).setState(FallingSand.METAL);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 100; i++) {
      game.step();
    }

    // Sand sinks past water, stops on metal. Water rises above sand.
    assertEquals(FallingSand.WATER, grid.getCell(0, 0).getState());
    assertEquals(FallingSand.SAND, grid.getCell(1, 0).getState());
    assertEquals(FallingSand.METAL, grid.getCell(2, 0).getState());
  }

  @Test
  void testSandStopsAtBottomEdge() {
    Grid grid = new Grid(2, 1);
    grid.getCell(0, 0).setState(FallingSand.SAND);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 100; i++) {
      game.step();
    }

    // Sand falls to bottom and stays
    assertEquals(FallingSand.SAND, grid.getCell(1, 0).getState());
  }

  @Test
  void testWaterFlowsSideways() {
    // Water in a 1-row-tall space with walls on sides should still be water
    // In a wider space, water spreads
    Grid grid = new Grid(2, 3);
    grid.getCell(0, 1).setState(FallingSand.WATER);
    grid.getCell(1, 0).setState(FallingSand.METAL);
    grid.getCell(1, 1).setState(FallingSand.METAL);
    grid.getCell(1, 2).setState(FallingSand.METAL);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 200; i++) {
      game.step();
    }

    // Water can't go down (metal), so it moves left or right
    // It should end up somewhere in row 0
    int waterCount = 0;
    for (int c = 0; c < 3; c++) {
      if (grid.getCell(0, c).getState() == FallingSand.WATER) {
        waterCount++;
      }
    }
    assertEquals(1, waterCount);
  }

  @Test
  void testParticleCountConserved() {
    Grid grid = new Grid(5, 5);
    grid.getCell(0, 2).setState(FallingSand.SAND);
    grid.getCell(0, 3).setState(FallingSand.SAND);
    grid.getCell(1, 1).setState(FallingSand.WATER);
    grid.getCell(1, 2).setState(FallingSand.WATER);
    grid.getCell(2, 2).setState(FallingSand.METAL);

    int initialSand = countState(grid, FallingSand.SAND);
    int initialWater = countState(grid, FallingSand.WATER);
    int initialMetal = countState(grid, FallingSand.METAL);

    FallingSand game = new FallingSand(grid);
    for (int i = 0; i < 200; i++) {
      game.step();
    }

    assertEquals(initialSand, countState(grid, FallingSand.SAND));
    assertEquals(initialWater, countState(grid, FallingSand.WATER));
    assertEquals(initialMetal, countState(grid, FallingSand.METAL));
  }

  @Test
  void testEmptyGridRemainsEmpty() {
    Grid grid = new Grid(3, 3);

    FallingSand game = new FallingSand(grid);
    game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(FallingSand.EMPTY, grid.getCell(i, j).getState());
      }
    }
  }

  private int countState(Grid grid, int state) {
    int count = 0;
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumns(); j++) {
        if (grid.getCell(i, j).getState() == state) {
          count++;
        }
      }
    }
    return count;
  }
}
