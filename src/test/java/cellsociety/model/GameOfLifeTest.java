package cellsociety.model;

import cellsociety.model.Grid;
import cellsociety.model.Cell;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameOfLifeTest {
    @org.junit.jupiter.api.Test
    void testLiveCellWithTwoNeighborsSurvives() {
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid);

        grid.getCell(1, 1).setState(1);
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    @org.junit.jupiter.api.Test
    void testDeadCellWithThreeNeighborsBecomeAlive() {
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid);

        // Center is dead, three neighbors alive
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(1, 0).setState(1);

        game.step();

        // Center should become alive
        assertEquals(1, grid.getCell(1, 1).getState());
    }
}
