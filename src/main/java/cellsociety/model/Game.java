package cellsociety.model;

import java.util.*;

public abstract class Game {
    protected Grid grid;

    public Game(Grid grid) {
        this.grid = grid;
    }

    public void step() {
        updateGame();
    }

    protected abstract void updateGame();

    public Grid getGrid() {
        return grid;
    }

    public Cell getCell(int row, int col) {
        return grid.getCell(row, col);
    }
}
