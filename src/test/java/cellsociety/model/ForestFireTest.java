package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForestFireTest {
    Grid defaultGrid = new Grid(3, 3);
    Game defaultGame = new ForestFire(defaultGrid, 0.0, 0.0);


  @org.junit.jupiter.api.Test
  void testTreeWithBurningNeighborsBurns() {

    defaultGrid.getCell(1, 1).setState(1);
    defaultGrid.getCell(0, 0).setState(2);

    defaultGame.step();

    assertEquals(2, defaultGrid.getCell(1, 1).getState());
  }

  @org.junit.jupiter.api.Test
  void testBurningTreeBecomesEmpty() {
    defaultGrid.getCell(1, 1).setState(2);
    defaultGame.step();

    assertEquals(0, defaultGrid.getCell(1, 1).getState());
  }

  //TODO: test behaviors with probabilities

}