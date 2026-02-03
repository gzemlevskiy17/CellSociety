package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GridTest {

  //getCell currently a trivial method
//  @org.junit.jupiter.api.Test
//  void getCell() {
//  }

  @org.junit.jupiter.api.Test
  void getNeighbors() {
    Grid defaultGrid = new Grid(5, 5);

    List<Cell> topLeftCornerNeighbors = new ArrayList<>(
        Arrays.asList(defaultGrid.getCell(0, 1), defaultGrid.getCell(1, 0), defaultGrid.getCell(1, 1)));
    assertEquals(topLeftCornerNeighbors, defaultGrid.getNeighbors(0, 0));

    List<Cell> middleCellNeighbors = new ArrayList<>(
        Arrays.asList(defaultGrid.getCell(1, 1), defaultGrid.getCell(1, 2),
            defaultGrid.getCell(1, 3), defaultGrid.getCell(2, 1),
            defaultGrid.getCell(2, 3), defaultGrid.getCell(3, 1),
            defaultGrid.getCell(3, 2), defaultGrid.getCell(3, 3)));
    assertEquals(middleCellNeighbors, defaultGrid.getNeighbors(2, 2));
  }

  @org.junit.jupiter.api.Test
  void isInBounds() {
    Grid defaultGrid = new Grid(5, 5);
    assertFalse(defaultGrid.isInBounds(-1, -1));
    assertTrue(defaultGrid.isInBounds(0, 0));
    assertTrue(defaultGrid.isInBounds(3, 2));
    assertFalse(defaultGrid.isInBounds(5, 5));
  }
}