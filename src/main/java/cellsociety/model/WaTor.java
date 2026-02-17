package cellsociety.model;

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
    private class Shark {
        private int energy;
        private int life = 0;

        public Shark(int energy) {
            this.energy = energy;
        }

        public boolean isDead() {
            return energy <= 0;
        }

        public int reProd() {
            return life;
        }

        public void reset() {
            life = 0;
        }

        public void moved(int eat) {
            energy += eat;
            life++;
        }
    }

    private class Fish {
        private int life = 0;

        public int reProd() {
            return life;
        }

        public void reset() {
            life = 0;
        }

        public void moved() {
            life++;
        }
    }

    private int pyRepro;
    private int pdrEner;
    private int pdrRepro;
    private int pdrEatsFor;
    private boolean moved = false;
    private HashMap<Cell, Shark> sharkLives = new HashMap<Cell, Shark>();
    private HashMap<Cell, Fish> fishLives = new HashMap<Cell, Fish>();

    public WaTor(Grid grid, Map<String, Double> params) {
        super(grid);
        this.pyRepro = params.get("pyRepro").intValue();
        this.pdrEner = params.get("pdrEner").intValue();
        this.pdrRepro = params.get("pdrRepro").intValue();
        this.pdrEatsFor = params.get("pdrEatsFor").intValue();
    }

    @Override
    protected void updateGame() {
        // first run the sharks
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.getState() == 2 && cell.getNextState() == 2) {
                    moved = false;
                    Shark sharky;
                    if (!sharkLives.containsKey(cell)) {
                        sharky = new Shark(pdrEner);
                        sharkLives.put(cell, sharky);
                    } else {
                        sharky = sharkLives.get(cell);
                    }
                    if (sharky.isDead()) {
                        sharkLives.remove(cell);
                        cell.setNextState(0);
                    } else {
                        Cell moves = sharkMoves(sharky, i, j);
                        sharky.moved(-1);
                        cell.setNextState(0);
                        moves.setNextState(2);
                        sharkLives.remove(cell);
                        sharkLives.put(moves, sharky);
                        if (moved && sharky.reProd() >= pdrRepro) {
                            sharky.reset();
                            cell.setNextState(2);
                            Shark child = new Shark(pdrEner);
                            sharkLives.put(cell, child);
                        }
                    }
                }
            }
        }
        // finally run the fish
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.getState() == 1 && cell.getNextState() == 1) {
                    moved = false;
                    Fish fishy;
                    if (!fishLives.containsKey(cell)) {
                        fishy = new Fish();
                        fishLives.put(cell, fishy);
                    } else {
                        fishy = fishLives.get(cell);
                    }
                    Cell moves = fishMoves(i, j);
                    cell.setNextState(0);
                    moves.setNextState(1);
                    fishLives.remove(cell);
                    fishLives.put(moves, fishy);
                    fishy.moved();
                    if (moved && fishy.reProd() >= pyRepro) {
                        fishy.reset();
                        cell.setNextState(1);
                        Fish child = new Fish();
                        fishLives.put(cell, child);
                    }
                }
            }
        }
    }

    private Cell sharkMoves(Shark sharky, int i, int j) {
        List<Cell> fishFood = grid.getNeighbors(i, j);
        fishFood = findFood(fishFood);
        if (!fishFood.isEmpty()) {
            Collections.shuffle(fishFood);
            sharky.moved(pdrEatsFor);
            fishLives.remove(fishFood.get(0));
            moved = true;
            return fishFood.get(0);
        } else {
            List<Cell> empty = grid.getNeighbors(i, j);
            empty = findEmpty(empty);
            if (!empty.isEmpty()) {
                Collections.shuffle(empty);
                moved = true;
                return empty.get(0);
            }
        }
        return grid.getCell(i, j);
    }

    private Cell fishMoves(int i, int j) {
        List<Cell> empty = grid.getNeighbors(i, j);
        empty = findEmpty(empty);
        if (!empty.isEmpty()) {
            Collections.shuffle(empty);
            moved = true;
            return empty.get(0);
        }
        return grid.getCell(i, j);

    }

    private List<Cell> findEmpty(List<Cell> empty) {
        List<Cell> ret = new ArrayList<Cell>();
        for (Cell looking : empty) {
            if (looking.getNextState() == 0) {
                ret.add(looking);
            }
        }
        return ret;
    }

    private List<Cell> findFood(List<Cell> fishFood) {

        List<Cell> ret = new ArrayList<Cell>();
        for (Cell looking : fishFood) {
            if (looking.getNextState() == 1) {
                ret.add(looking);
            }
        }
        return ret;
    }

}
