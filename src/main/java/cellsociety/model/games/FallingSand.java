package cellsociety.model.games;

import cellsociety.model.Game;
import cellsociety.model.Grid;

import java.util.Map;
import java.util.Random;

/**
 * Falling Sand simulation. Particles fall and interact under simulated gravity.
 *
 * <p>
 * States: EMPTY (0), METAL (1), SAND (2), WATER (3).
 * </p>
 *
 * <p>
 * Uses sequential random updates inside updateGame(). Each step picks
 * rows*cols random cells and immediately moves them using setState(), so
 * subsequent picks see the updated getGrid(). commitChanges() is harmless since
 * setState() keeps state and nextState in sync.
 * </p>
 */
public class FallingSand extends Game {

  public static final int EMPTY = 0;
  public static final int METAL = 1;
  public static final int SAND = 2;
  public static final int WATER = 3;

  private final Random random = new Random();

  public FallingSand(Grid grid) {
    super(grid);
  }

  public FallingSand(Grid grid, Map<String, String> params) {
    super(grid, params);
  }

  @Override
  protected void updateGame() {
    int rows = getGrid().getRows();
    int cols = getGrid().getColumns();
    int iterations = rows * cols;
    for (int i = 0; i < iterations; i++) {
      int r = random.nextInt(rows);
      int c = random.nextInt(cols);
      int state = getGrid().getCell(r, c).getState();
      if (state == SAND) {
        moveSand(r, c);
      } else if (state == WATER) {
        moveWater(r, c);
      }
    }
  }

  private void moveSand(int row, int col) {
    int belowRow = row + 1;
    if (!getGrid().isInBounds(belowRow, col)) {
      return;
    }
    int belowState = getGrid().getCell(belowRow, col).getState();
    if (belowState == EMPTY || belowState == WATER) {
      swap(row, col, belowRow, col);
    }
  }

  private void moveWater(int row, int col) {
    int direction = random.nextInt(3);
    int targetRow = row;
    int targetCol = col;
    if (direction == 0) {
      targetRow = row + 1;
    } else if (direction == 1) {
      targetCol = col - 1;
    } else {
      targetCol = col + 1;
    }
    if (!getGrid().isInBounds(targetRow, targetCol)) {
      return;
    }
    if (getGrid().getCell(targetRow, targetCol).getState() == EMPTY) {
      swap(row, col, targetRow, targetCol);
    }
  }

  private void swap(int r1, int c1, int r2, int c2) {
    int temp = getGrid().getCell(r1, c1).getState();
    getGrid().getCell(r1, c1).setState(getGrid().getCell(r2, c2).getState());
    getGrid().getCell(r2, c2).setState(temp);
  }
}
