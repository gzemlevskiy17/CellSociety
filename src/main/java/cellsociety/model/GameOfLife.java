package cellsociety.model;

import java.util.*;

public class GameOfLife extends Game {
    public GameOfLife(Grid grid) {
        super(grid);
    }

    @Override
    protected void updateGame() {
        Grid nextStates = new Grid(grid.getRows(), grid.getCols());

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                List<Cell> neighbors = grid.getNeighbors(i, j);
                int numAliveNeighbors = getAliveNeighbors(neighbors);

                int nextState = 0;

                if (cell.getState() == 1) {
                    if (numAliveNeighbors == 2 || numAliveNeighbors == 3) {
                        nextState = 1;
                    }
                } else {
                    if (numAliveNeighbors == 3) {
                        nextState = 1;
                    }
                }

                nextStates.getCell(i, j).setState(nextState);
            }
        }

        commitChanges(grid, nextStates);

    }

    private void commitChanges(Grid grid, Grid nextStates) {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                grid.getCell(i, j).setState(nextStates.getCell(i, j).getState());
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
