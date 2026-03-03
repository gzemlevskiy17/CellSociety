package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wa-Tor World simulation.
 *
 * <p>
 * Uses three states: 0 (empty), 1 (fish), 2 (shark). Transition rules:
 * </p>
 * <ul>
 * <li>For fish, at each tick, a fish moves randomly to an adjacent unoccupied
 * squares. If there are no free squares, no movement takes place.</li>
 * <li>For fish and sharks, once they have survived {@code pyRepro} or
 * {@code pdrRepro} number of ticks, respectively, it may reproduce. This is
 * done as it moves to a neighbouring square, leaving behind a new creature. Its
 * reproduction time is also reset to zero.</li>
 * <li>For shark, at each tick, a shark moves randomly to an adjacent square
 * occupied by a fish. If there is none, the shark moves to a random adjacent
 * unoccupied square. If there are no free squares, no movement takes
 * place.</li>
 * <li>At each tick, each shark is deprived of a unit of energy. They start with
 * {@code pdrEner} energy.</li>
 * <li>Upon reaching zero energy, a shark dies.</li>
 * <li>If a shark moves to a square occupied by a fish, it eats the fish and
 * earns {@code pderEatsFor} amount of energy.</li>
 * </ul>
 *
 * <p>
 * <b>Assumptions:</b> Probabilities should be in the range [0.0, 1.0].
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}
 * </p>
 *
 * @author Joseph Wang
 * @see Game
 */

public class WaTor extends Game {

  public static final int EMPTY = 0;
  public static final int FISH = 1;
  public static final int SHARK = 2;
  private final String[] newVariables = new String[]{"pyRepro", "pdrEner", "pdrRepro",
      "pdrEatsFor"};
  private boolean moved = false;
  private HashMap<Cell, Shark> sharkLives = new HashMap<Cell, Shark>();
  private HashMap<Cell, Fish> fishLives = new HashMap<Cell, Fish>();

  public WaTor(Grid grid, Map<String, String> params) throws Exception {
    super(grid, params);
    int val = -1;
    for (String vari : newVariables) {
      try {
        val = Integer.parseInt(getParam(vari));
      } catch (Exception e) {
        throw new IllegalArgumentException("Illegal " + vari + ": " + getParam(vari));
      }

      if (val < 0) {
        throw new IllegalArgumentException("Illegal " + vari + ": " + getParam(vari));
      }
    }
  }

  @Override
  protected void updateGame() {
    // first run the sharks
    sharkMove();
    // finally run the fish
    fishMove();
  }

  private void sharkMove() {
    for (int i = 0; i < getGrid().getRows(); i++) {
      for (int j = 0; j < getGrid().getColumns(); j++) {
        Cell cell = getGrid().getCell(i, j);
        if (cell.getState() == SHARK && cell.getNextState() == SHARK) {
          moved = false;
          Shark shark;
          if (!sharkLives.containsKey(cell)) {
            shark = new Shark((int) Double.parseDouble(getParam(newVariables[1])));
            sharkLives.put(cell, shark);
          } else {
            shark = sharkLives.get(cell);
          }
          if (shark.isDead()) {
            sharkLives.remove(cell);
            cell.setNextState(EMPTY);
          } else {
            Cell moves = sharkMoves(shark, i, j);
            shark.moved(-1);
            cell.setNextState(EMPTY);
            moves.setNextState(SHARK);
            sharkLives.remove(cell);
            sharkLives.put(moves, shark);
            if (moved && shark.reProd() >= (int) Double.parseDouble(getParam(newVariables[2]))) {
              shark.reset();
              cell.setNextState(2);
              Shark child = new Shark((int) Double.parseDouble(getParam(newVariables[1])));
              sharkLives.put(cell, child);
            }
          }
        }
      }
    }
  }

  public void fishMove() {
    for (int i = 0; i < getGrid().getRows(); i++) {
      for (int j = 0; j < getGrid().getColumns(); j++) {
        Cell cell = getGrid().getCell(i, j);
        if (cell.getState() == FISH && cell.getNextState() == FISH) {
          moved = false;
          Fish fishy;
          if (!fishLives.containsKey(cell)) {
            fishy = new Fish();
            fishLives.put(cell, fishy);
          } else {
            fishy = fishLives.get(cell);
          }
          Cell moves = fishMoves(i, j);
          cell.setNextState(EMPTY);
          moves.setNextState(FISH);
          fishLives.remove(cell);
          fishLives.put(moves, fishy);
          fishy.moved();
          if (moved && fishy.reProd() >= (int) Double.parseDouble(getParam(newVariables[0]))) {
            fishy.reset();
            cell.setNextState(FISH);
            Fish child = new Fish();
            fishLives.put(cell, child);
          }
        }
      }
    }
  }

  private Cell sharkMoves(Shark sharky, int i, int j) {
    List<Cell> fishFood = getGrid().getNeighbors(i, j);
    fishFood = findFood(fishFood);
    if (!fishFood.isEmpty()) {
      Collections.shuffle(fishFood);
      sharky.moved((int) Double.parseDouble(getParam(newVariables[3])));
      fishLives.remove(fishFood.getFirst());
      moved = true;
      return fishFood.getFirst();
    } else {
      List<Cell> empty = getGrid().getNeighbors(i, j);
      empty = findEmpty(empty);
      if (!empty.isEmpty()) {
        Collections.shuffle(empty);
        moved = true;
        return empty.getFirst();
      }
    }
    return getGrid().getCell(i, j);
  }

  private Cell fishMoves(int i, int j) {
    List<Cell> empty = getGrid().getNeighbors(i, j);
    empty = findEmpty(empty);
    if (!empty.isEmpty()) {
      Collections.shuffle(empty);
      moved = true;
      return empty.getFirst();
    }
    return getGrid().getCell(i, j);

  }

  private List<Cell> findEmpty(List<Cell> empty) {
    List<Cell> ret = new ArrayList<Cell>();
    for (Cell looking : empty) {
      if (looking.getNextState() == EMPTY) {
        ret.add(looking);
      }
    }
    return ret;
  }

  private List<Cell> findFood(List<Cell> fishFood) {

    List<Cell> ret = new ArrayList<Cell>();
    for (Cell looking : fishFood) {
      if (looking.getNextState() == FISH) {
        ret.add(looking);
      }
    }
    return ret;
  }

  private class Shark {

    private int energy;
    private int life = 0;

    public Shark(int energy) {
      this.energy = energy;
    }

    private boolean isDead() {
      return energy <= 0;
    }

    private int reProd() {
      return life;
    }

    private void reset() {
      life = 0;
    }

    private void moved(int eat) {
      energy += eat;
      life++;
    }
  }

  private class Fish {

    private int life = 0;

    private int reProd() {
      return life;
    }

    private void reset() {
      life = 0;
    }

    private void moved() {
      life++;
    }
  }

}
