package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.games.ForestFire;
import org.junit.jupiter.api.Test;

class ForestFireTest {
    Grid defaultGrid = new Grid(3, 3);
    Game defaultGame = new ForestFire(defaultGrid, 0.0, 0.0);

  //TODO: follow consistent naming convention from PercolationTest
  //TODO: add more negative tests?

  @Test
  void Step_TreeWithBurningNeighbors_Burns() {

    defaultGrid.getCell(1, 1).setState(1);
    defaultGrid.getCell(0, 0).setState(2);

    defaultGame.step();

    assertEquals(2, defaultGrid.getCell(1, 1).getState());
  }

  @Test
  void Step_BurningTree_BecomesEmpty() {
    defaultGrid.getCell(1, 1).setState(2);
    defaultGame.step();

    assertEquals(0, defaultGrid.getCell(1, 1).getState());
  }

  @Test
  void Step_TreeIgnitionProbability1_Burns() {
    Grid treeGrid = new Grid(3, 3);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        treeGrid.getCell(i, j).setState(1);
      }
    }

    Game ignitionProbability1Game = new ForestFire(treeGrid, 1.0, 0.0);
    ignitionProbability1Game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(2, ignitionProbability1Game.getCell(i, j).getState());
      }
    }
  }

  @Test
  void Step_TreeFillProbability1_Fills() {
    Game treeFillProbability1Game = new ForestFire(defaultGrid, 0.0, 1.0);
    treeFillProbability1Game.step();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(1, treeFillProbability1Game.getCell(i, j).getState());
      }
    }
  }
}