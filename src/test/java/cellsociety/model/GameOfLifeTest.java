package cellsociety.model;

import cellsociety.model.games.GameOfLife;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GameOfLifeTest {

    // --- Classic Conway (B3/S23, default) ---

    @Test
    void testLiveCellWithTwoNeighborsSurvives() {
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid);

        grid.getCell(1, 1).setState(1);
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    @Test
    void testDeadCellWithThreeNeighborsBecomeAlive() {
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid);

        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(1, 0).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    // --- B/S notation parsing ---

    @Test
    void testBSNotationMatchesDefault() {
        // B3/S23 is the same as classic Conway
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "B3/S23");

        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(1, 0).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    @Test
    void testSeedsRuleBirthAtTwo() {
        // B2/S: dead cell with exactly 2 neighbors is born, all live cells die
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "B2/S");

        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 2).setState(1);
        // (0,1) has 2 alive neighbors -> should be born
        game.step();

        assertEquals(1, grid.getCell(0, 1).getState());
        // Live cells die (empty survival list)
        assertEquals(0, grid.getCell(0, 0).getState());
        assertEquals(0, grid.getCell(0, 2).getState());
    }

    @Test
    void testMazeRuleSurvivesWithFiveNeighbors() {
        // B3/S12345: live cell with 5 neighbors survives
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "B3/S12345");

        // Center alive with 5 alive neighbors
        grid.getCell(1, 1).setState(1);
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(0, 2).setState(1);
        grid.getCell(1, 0).setState(1);
        grid.getCell(1, 2).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    // --- S/B notation parsing ---

    @Test
    void testSBNotationParsesCorrectly() {
        // "23/3" in S/B notation = survival 2,3 / birth 3 = same as B3/S23
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "23/3");

        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(1, 0).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    @Test
    void testSBNotationNoBirthKillsAll() {
        // "23/" in S/B notation = survival 2,3 / birth never
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "23/");

        // Dead cell with 3 neighbors should NOT be born (empty birth list)
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(1, 0).setState(1);
        game.step();

        assertEquals(0, grid.getCell(1, 1).getState());
    }

    // --- HighLife B36/S23 ---

    @Test
    void testHighLifeBirthAtSix() {
        // B36/S23: dead cell with 6 neighbors is born (in addition to 3)
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "B36/S23");

        // Center dead, 6 alive neighbors
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 1).setState(1);
        grid.getCell(0, 2).setState(1);
        grid.getCell(1, 0).setState(1);
        grid.getCell(1, 2).setState(1);
        grid.getCell(2, 1).setState(1);
        game.step();

        assertEquals(1, grid.getCell(1, 1).getState());
    }

    // --- Error handling ---

    @Test
    void testInvalidRulestringThrows() {
        Grid grid = new Grid(3, 3);
        assertThrows(IllegalArgumentException.class,
            () -> new GameOfLife(grid, "invalid"));
    }

    // --- Default (no rulestring) behaves as classic ---

    @Test
    void testEmptyRulestringDefaultsToConway() {
        Grid grid = new Grid(3, 3);
        GameOfLife game = new GameOfLife(grid, "");

        // Dead cell with 2 neighbors should NOT be born under Conway
        grid.getCell(0, 0).setState(1);
        grid.getCell(0, 2).setState(1);
        game.step();

        assertEquals(0, grid.getCell(0, 1).getState());
    }
}
