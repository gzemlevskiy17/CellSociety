package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;

import java.util.ArrayList;
import java.util.Collections;
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

    private static final String[] newVariables = new String[] { "pyRepro", "pdrEner", "pdrRepro", "pdrEatsFor" };
    private boolean moved = false;
    private Map<Cell, WaTorShark> sharkLives = new java.util.HashMap<>();
    private Map<Cell, WaTorFish> fishLives = new java.util.HashMap<>();

    public WaTor(Grid grid, Map<String, String> params) {
        super(grid, params);
        int val = -1;
        for (String vari : newVariables) {
            try {
                val = (int) Double.parseDouble(getParam(vari));
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
        sharkMove();
        fishMove();
    }

    private void sharkMove() {
        for (int i = 0; i < getGrid().getRows(); i++) {
            for (int j = 0; j < getGrid().getColumns(); j++) {
                Cell cell = getGrid().getCell(i, j);
                if (cell.getState() == SHARK && cell.getNextState() == SHARK) {
                    moved = false;
                    WaTorShark sharky;
                    if (!sharkLives.containsKey(cell)) {
                        sharky = new WaTorShark((int) Double.parseDouble(getParam(newVariables[1])));
                        sharkLives.put(cell, sharky);
                    } else {
                        sharky = sharkLives.get(cell);
                    }
                    if (sharky.isDead()) {
                        sharkLives.remove(cell);
                        cell.setNextState(EMPTY);
                    } else {
                        Cell moves = sharkMoves(sharky, i, j);
                        sharky.moved(-1);
                        cell.setNextState(EMPTY);
                        moves.setNextState(SHARK);
                        sharkLives.remove(cell);
                        sharkLives.put(moves, sharky);
                        if (moved && sharky.reProd() >= (int) Double.parseDouble(getParam(newVariables[2]))) {
                            sharky.reset();
                            cell.setNextState(SHARK);
                            WaTorShark child = new WaTorShark((int) Double.parseDouble(getParam(newVariables[1])));
                            sharkLives.put(cell, child);
                        }
                    }
                }
            }
        }
    }

    private void fishMove() {
        for (int i = 0; i < getGrid().getRows(); i++) {
            for (int j = 0; j < getGrid().getColumns(); j++) {
                Cell cell = getGrid().getCell(i, j);
                if (cell.getState() == FISH && cell.getNextState() == FISH) {
                    moved = false;
                    WaTorFish fishy;
                    if (!fishLives.containsKey(cell)) {
                        fishy = new WaTorFish();
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
                        WaTorFish child = new WaTorFish();
                        fishLives.put(cell, child);
                    }
                }
            }
        }
    }

    private Cell sharkMoves(WaTorShark sharky, int i, int j) {
        List<Cell> fishFood = filterByState(getGrid().getNeighbors(i, j), FISH);
        if (!fishFood.isEmpty()) {
            Collections.shuffle(fishFood);
            sharky.moved((int) Double.parseDouble(getParam(newVariables[3])));
            fishLives.remove(fishFood.get(0));
            moved = true;
            return fishFood.get(0);
        }
        List<Cell> empty = filterByState(getGrid().getNeighbors(i, j), EMPTY);
        if (!empty.isEmpty()) {
            Collections.shuffle(empty);
            moved = true;
            return empty.get(0);
        }
        return getGrid().getCell(i, j);
    }

    private Cell fishMoves(int i, int j) {
        List<Cell> empty = filterByState(getGrid().getNeighbors(i, j), EMPTY);
        if (!empty.isEmpty()) {
            Collections.shuffle(empty);
            moved = true;
            return empty.get(0);
        }
        return getGrid().getCell(i, j);
    }

    private List<Cell> filterByState(List<Cell> cells, int targetState) {
        List<Cell> ret = new ArrayList<>();
        for (Cell looking : cells) {
            if (looking.getNextState() == targetState) {
                ret.add(looking);
            }
        }
        return ret;
    }
}
