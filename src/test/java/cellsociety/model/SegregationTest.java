package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.games.Segregation;
import org.junit.jupiter.api.Test;

class SegregationTest {

  @Test
  void testSatisfiedCellDoesNotMove() {
    Grid grid = new Grid(3, 3);
    // Center cell (state 1) surrounded by same-type neighbors
    grid.getCell(1, 1).setState(1);
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(1);
    grid.getCell(1, 0).setState(1);

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    assertEquals(1, grid.getCell(1, 1).getState());
  }

  @Test
  void testDissatisfiedCellMovesToEmptyCell() {
    Grid grid = new Grid(3, 3);
    // Center is state 1, all neighbors are state 2 -> dissatisfied
    grid.getCell(1, 1).setState(1);
    grid.getCell(0, 0).setState(2);
    grid.getCell(0, 1).setState(2);
    grid.getCell(1, 0).setState(2);
    grid.getCell(0, 2).setState(2);

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    // Center cell should have moved away (become empty)
    assertEquals(0, grid.getCell(1, 1).getState());
  }

  @Test
  void testEmptyCellRemainsEmptyWhenNoMovement() {
    Grid grid = new Grid(3, 3);
    // All cells satisfied, empty cells should stay empty
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(1);
    grid.getCell(1, 0).setState(1);

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    assertEquals(0, grid.getCell(2, 2).getState());
  }

  @Test
  void testCellWithNoNeighborsIsSatisfied() {
    Grid grid = new Grid(3, 3);
    // Isolated cell with only empty neighbors is satisfied (totalCount == 0)
    grid.getCell(1, 1).setState(1);

    Segregation game = new Segregation(grid, 1.0);
    game.step();

    assertEquals(1, grid.getCell(1, 1).getState());
  }

  @Test
  void testHighThresholdCausesDissatisfaction() {
    Grid grid = new Grid(3, 3);
    // Center is state 1, one same neighbor, one different -> ratio = 0.5
    grid.getCell(1, 1).setState(1);
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(2);

    // Threshold 0.8 means 0.5 ratio is not enough -> dissatisfied
    Segregation game = new Segregation(grid, 0.8);
    game.step();

    assertEquals(0, grid.getCell(1, 1).getState());
  }

  @Test
  void testLowThresholdKeepsCellSatisfied() {
    Grid grid = new Grid(3, 3);
    // Center is state 1, one same neighbor, two different -> ratio = 1/3
    grid.getCell(1, 1).setState(1);
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(2);
    grid.getCell(1, 0).setState(2);

    // Threshold 0.3 means 1/3 ratio is enough -> satisfied
    Segregation game = new Segregation(grid, 0.3);
    game.step();

    assertEquals(1, grid.getCell(1, 1).getState());
  }

  @Test
  void testPopulationIsConservedAfterStep() {
    Grid grid = new Grid(4, 4);
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(2);
    grid.getCell(1, 0).setState(2);
    grid.getCell(1, 1).setState(1);
    grid.getCell(2, 0).setState(1);
    grid.getCell(3, 3).setState(2);

    int initialOnes = countState(grid, 1);
    int initialTwos = countState(grid, 2);
    int initialEmpty = countState(grid, 0);

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    assertEquals(initialOnes, countState(grid, 1));
    assertEquals(initialTwos, countState(grid, 2));
    assertEquals(initialEmpty, countState(grid, 0));
  }

  @Test
  void testAllSameTypeNoMovement() {
    Grid grid = new Grid(3, 3);
    // Fill entire grid with state 1 -> everyone satisfied
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid.getCell(i, j).setState(1);
      }
    }

    Segregation game = new Segregation(grid, 1.0);
    game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(1, grid.getCell(i, j).getState());
      }
    }
  }

  @Test
  void testDissatisfiedCellMovesToAnEmptySpot() {
    Grid grid = new Grid(3, 3);
    // One state-1 cell surrounded by state-2, with one empty cell available
    grid.getCell(0, 0).setState(1);
    grid.getCell(0, 1).setState(2);
    grid.getCell(1, 0).setState(2);
    grid.getCell(1, 1).setState(2);
    // Only (2,2) is empty

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    // The dissatisfied cell (0,0) should move; check it vacated
    assertEquals(0, grid.getCell(0, 0).getState());
    // The agent should appear somewhere on the grid
    assertEquals(1, countState(grid, 1));
  }

  @Test
  void testZeroThresholdMeansAlwaysSatisfied() {
    Grid grid = new Grid(3, 3);
    grid.getCell(1, 1).setState(1);
    grid.getCell(0, 0).setState(2);
    grid.getCell(0, 1).setState(2);
    grid.getCell(1, 0).setState(2);

    // With threshold 0.0, any ratio satisfies
    Segregation game = new Segregation(grid, 0.0);
    game.step();

    assertEquals(1, grid.getCell(1, 1).getState());
  }

  @Test
  void testAllEmptyGridRemainsEmpty() {
    Grid grid = new Grid(3, 3);
    // All cells default to state 0 (empty)

    Segregation game = new Segregation(grid, 0.5);
    game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(0, grid.getCell(i, j).getState());
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
