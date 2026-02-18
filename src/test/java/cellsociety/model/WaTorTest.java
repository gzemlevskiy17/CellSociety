package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WaTorTest {

  private static final int EMPTY = 0;
  private static final int FISH = 1;
  private static final int SHARK = 2;

  private Map<String, Double> defaultParams() {
    Map<String, Double> params = new HashMap<>();
    params.put("pyRepro", 3.0);
    params.put("pdrEner", 5.0);
    params.put("pdrRepro", 8.0);
    params.put("pdrEatsFor", 3.0);
    params.put("speed", 1.0);
    return params;
  }

  @Test
  void testFishMovesToEmptyNeighbor() {
    // Single fish in center, surrounded by empty — should move after a step
    Grid grid = new Grid(3, 3);
    grid.getCell(1, 1).setState(FISH);

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    // Fish should have left center
    int fishCount = countState(grid, FISH);
    assertEquals(1, fishCount);
    // It moved somewhere (may or may not still be center if no empty found,
    // but here there are 8 empty neighbors so it must have moved)
    assertEquals(EMPTY, grid.getCell(1, 1).getState());
  }

  @Test
  void testSharkEatsAdjacentFish() {
    // Shark next to a fish — shark should eat it
    Grid grid = new Grid(1, 3);
    grid.getCell(0, 0).setState(SHARK);
    grid.getCell(0, 1).setState(FISH);

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    // Fish should be gone, shark should have moved to fish's location
    assertEquals(0, countState(grid, FISH));
    assertEquals(1, countState(grid, SHARK));
  }

  @Test
  void testSharkDiesWhenEnergyDepleted() {
    // Shark with energy 1, no fish to eat — should die after 1 step
    Map<String, Double> params = defaultParams();
    params.put("pdrEner", 1.0);
    params.put("pdrEatsFor", 0.0);

    Grid grid = new Grid(1, 3);
    grid.getCell(0, 1).setState(SHARK);

    WaTor game = new WaTor(grid, params);
    // After step, shark loses 1 energy -> 0 energy
    game.step();
    // After another step, shark should be dead (energy <= 0)
    game.step();

    assertEquals(0, countState(grid, SHARK));
  }

  @Test
  void testFishReproducesAfterEnoughTicks() {
    // Fish with pyRepro = 1, should reproduce after 1 tick
    Map<String, Double> params = defaultParams();
    params.put("pyRepro", 1.0);

    Grid grid = new Grid(3, 3);
    grid.getCell(1, 1).setState(FISH);

    WaTor game = new WaTor(grid, params);
    game.step();

    // Should now be 2 fish: original moved + offspring left behind
    assertEquals(2, countState(grid, FISH));
  }

  @Test
  void testSharkReproducesAfterEnoughTicks() {
    // Shark with pdrRepro = 1, should reproduce after 1 tick
    Map<String, Double> params = defaultParams();
    params.put("pdrRepro", 1.0);
    params.put("pdrEner", 10.0);

    Grid grid = new Grid(3, 3);
    grid.getCell(1, 1).setState(SHARK);

    WaTor game = new WaTor(grid, params);
    game.step();

    // Should now be 2 sharks: original moved + offspring left behind
    assertEquals(2, countState(grid, SHARK));
  }

  @Test
  void testEmptyGridRemainsEmpty() {
    Grid grid = new Grid(3, 3);

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(EMPTY, grid.getCell(i, j).getState());
      }
    }
  }

  @Test
  void testFishSurroundedByFishDoesNotMove() {
    // All cells are fish — no empty space to move to
    Grid grid = new Grid(3, 3);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid.getCell(i, j).setState(FISH);
      }
    }

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    // All should still be fish (no empty space to move or reproduce into)
    assertEquals(9, countState(grid, FISH));
  }

  @Test
  void testSharkEatingGainsEnergy() {
    // Shark with energy 2, eatsFor 3 — eating should keep it alive longer
    Map<String, Double> params = defaultParams();
    params.put("pdrEner", 2.0);
    params.put("pdrEatsFor", 3.0);

    Grid grid = new Grid(1, 3);
    grid.getCell(0, 0).setState(SHARK);
    grid.getCell(0, 1).setState(FISH);

    WaTor game = new WaTor(grid, params);
    game.step();

    // Shark ate fish, gained energy — should still be alive
    assertEquals(1, countState(grid, SHARK));
    assertEquals(0, countState(grid, FISH));

    // Run several more steps — shark should survive longer thanks to eating
    game.step();
    game.step();
    assertEquals(1, countState(grid, SHARK));
  }

  @Test
  void testEdgeFishCanMove() {
    // Fish in corner — only 3 neighbors, should still move
    Grid grid = new Grid(3, 3);
    grid.getCell(0, 0).setState(FISH);

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    assertEquals(1, countState(grid, FISH));
    assertEquals(EMPTY, grid.getCell(0, 0).getState());
  }

  @Test
  void testEdgeSharkCanEat() {
    // Shark in corner with fish neighbor
    Grid grid = new Grid(3, 3);
    grid.getCell(0, 0).setState(SHARK);
    grid.getCell(0, 1).setState(FISH);

    WaTor game = new WaTor(grid, defaultParams());
    game.step();

    assertEquals(0, countState(grid, FISH));
    assertEquals(1, countState(grid, SHARK));
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
