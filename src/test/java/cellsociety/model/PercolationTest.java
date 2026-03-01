package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.games.Percolation;
import org.junit.jupiter.api.Test;

class PercolationTest {

  //TODO: define into enum or constants in the concrete game classes (or abstract if enum)
  private static final int OPEN = 0;
  private static final int CLOSED = 1;
  private static final int PERCOLATED = 2;

  private Grid grid;
  private Percolation percolation;


  private int[][] snapshotGrid(GridReader reader) {
    int[][] snapshot = new int[reader.getRows()][reader.getCols()];
    for (int r = 0; r < reader.getRows(); r++) {
      for (int c = 0; c < reader.getCols(); c++) {
        snapshot[r][c] = reader.getCellState(r, c);
      }
    }
    return snapshot;
  }

  //TODO: add this to 'helper' class so that it can be used by other test classes?
  private void setupGrid(int[][] states) {
    int rows = states.length;
    int cols = states[0].length;
    grid = new Grid(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        grid.getCell(i, j).setState(states[i][j]);
      }
    }
    percolation = new Percolation(grid);
  }

  @Test
  void closedCell_SurroundedByPercolated_StaysClosed() {
    int[][] initial = {
        {PERCOLATED, PERCOLATED, PERCOLATED},
        {PERCOLATED, CLOSED, PERCOLATED},
        {PERCOLATED, PERCOLATED, PERCOLATED}
    };
    setupGrid(initial);

    percolation.step();

    assertEquals(CLOSED, grid.getCell(1, 1).getState());
  }

  @Test
  void percolatedCell_AfterStep_StaysPercolated() {
    int[][] initial = {
        {PERCOLATED, OPEN, OPEN},
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN}
    };
    setupGrid(initial);

    percolation.step();

    assertEquals(PERCOLATED, grid.getCell(0, 0).getState());
  }


  @Test
  void percolation_FromTop_SpreadsDownward() {
    int[][] initial = {
        {PERCOLATED, OPEN, OPEN},
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN}
    };
    setupGrid(initial);

    percolation.step();

    assertEquals(PERCOLATED, grid.getCell(0, 1).getState());
    assertEquals(PERCOLATED, grid.getCell(1, 0).getState());
    assertEquals(PERCOLATED, grid.getCell(1, 1).getState());
  }

  @Test
  void grid_MixedClosedAndPercolated_NoChangesOnStep() {
    int[][] initial = {
        {PERCOLATED, CLOSED, PERCOLATED},
        {CLOSED, CLOSED, CLOSED},
        {PERCOLATED, CLOSED, PERCOLATED}
    };
    setupGrid(initial);

    GridReader reader = new GridReader(grid);
    int[][] beforeStep = snapshotGrid(reader);
    percolation.step();
    int[][] afterStep = snapshotGrid(reader);

    assertArrayEquals(beforeStep, afterStep);
  }

  @Test
  void openCell_BlockedByClosedCells_DoesNotPercolate() {
    int[][] initial = {
        {PERCOLATED, CLOSED, OPEN},
        {CLOSED, CLOSED, OPEN},
        {OPEN, OPEN, OPEN}
    };
    setupGrid(initial);

    percolation.step();

    assertEquals(OPEN, grid.getCell(0, 2).getState());
    assertEquals(OPEN, grid.getCell(1, 2).getState());
    assertEquals(OPEN, grid.getCell(2, 0).getState());
  }
}