package cellsociety.model;

import java.util.*;

/**
 * Conway's Game of Life simulation.
 *
 * <p>Uses two states: 0 (dead) and 1 (alive). Transition rules:</p>
 * <ul>
 *   <li>An alive cell with 2 or 3 alive neighbors survives; otherwise it dies.</li>
 *   <li>A dead cell with exactly 3 alive neighbors becomes alive.</li>
 * </ul>
 *
 * <p><b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}</p>
 *
 * @author Michael Li
 * @see Game
 */
public class GameOfLife extends Game {

    /**
     * Constructs a Game of Life simulation on the given grid.
     *
     * @param grid the grid of cells; states should be 0 (dead) or 1 (alive)
     */
    public GameOfLife(Grid grid) {
        super(grid);
    }

    @Override
    protected void updateGame() {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                int numAliveNeighbors = getAliveNeighbors(grid.getNeighbors(i, j));

                if (cell.getState() == 1) {
                    cell.setNextState((numAliveNeighbors == 2 || numAliveNeighbors == 3) ? 1 : 0);
                } else {
                    cell.setNextState(numAliveNeighbors == 3 ? 1 : 0);
                }
            }
        }
    }

    private int getAliveNeighbors(List<Cell> neighbors) {
        int count = 0;
        for (Cell neighbor : neighbors) {
            if (neighbor.getState() == 1) {
                count += 1;
            }
        }
        return count;
    }
}
