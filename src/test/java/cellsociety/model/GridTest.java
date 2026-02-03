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

  //TODO: split tests into own methods
    Grid defaultGrid = new Grid(5, 5);



  @org.junit.jupiter.api.Test
  void getNeighborsTopLeftCorner() {
    List<Cell> topLeftCornerNeighbors = new ArrayList<>(
        Arrays.asList(defaultGrid.getCell(0, 1), defaultGrid.getCell(1, 0), defaultGrid.getCell(1, 1)));
    assertEquals(topLeftCornerNeighbors, defaultGrid.getNeighbors(0, 0));
  }

  @org.junit.jupiter.api.Test
  void getNeighborsMiddleCell() {
    List<Cell> middleCellNeighbors = new ArrayList<>(
        Arrays.asList(defaultGrid.getCell(1, 1), defaultGrid.getCell(1, 2),
            defaultGrid.getCell(1, 3), defaultGrid.getCell(2, 1),
            defaultGrid.getCell(2, 3), defaultGrid.getCell(3, 1),
            defaultGrid.getCell(3, 2), defaultGrid.getCell(3, 3)));
    assertEquals(middleCellNeighbors, defaultGrid.getNeighbors(2, 2));
  }

  @org.junit.jupiter.api.Test
  void isInBoundsNegative() {
    assertFalse(defaultGrid.isInBounds(-1, -1));
  }

  @org.junit.jupiter.api.Test
  void isInBoundsOrigin() {
    assertTrue(defaultGrid.isInBounds(0, 0));
  }

  @org.junit.jupiter.api.Test
  void isInBoundsInside() {
    assertTrue(defaultGrid.isInBounds(3, 2));
  }

  @org.junit.jupiter.api.Test
  void isInBoundsOutOfRange() {
    assertFalse(defaultGrid.isInBounds(5, 5));
  }
}