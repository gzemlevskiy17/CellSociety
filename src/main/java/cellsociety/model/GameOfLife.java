package cellsociety.model;

import java.util.List;

public class GameOfLife extends Game {
    public GameOfLife(Grid grid) {
        super(grid);
    }

    @Override
    protected void updateGame() {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
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
