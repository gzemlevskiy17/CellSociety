package cellsociety.model;

public abstract class Game {
    protected Grid grid;

    public Game(Grid grid) {
        this.grid = grid;
    }

    public void step() {
        updateGame();
        commitChanges();
    }

    protected abstract void updateGame();

    protected void commitChanges() {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                grid.getCell(i, j).updateState();
            }
        }
    }

    public Grid getGrid() {
        return grid;
    }

    public Cell getCell(int row, int col) {
        return grid.getCell(row, col);
    }

    //TODO: does this work for all types of games?
    public void clearGrid() {
        grid.defaultInitializeCells();
    }
}
