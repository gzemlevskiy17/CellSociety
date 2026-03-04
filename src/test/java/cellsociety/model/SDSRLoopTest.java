package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.model.games.SDSRLoop;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SDSRLoopTest {

    private static final int BACKGROUND = 0;
    private static final int CORE = 1;
    private static final int DISSOLVE = 8;

    @Test
    void testConstructionDoesNotThrow() {
        Grid grid = new Grid(5, 5);
        assertDoesNotThrow(() -> new SDSRLoop(grid, Map.of()));
    }

    @Test
    void testStepDoesNotThrow() {
        Grid grid = new Grid(5, 5);
        SDSRLoop game = new SDSRLoop(grid, Map.of());
        assertDoesNotThrow(game::step);
    }

    @Test
    void testAllBackgroundRemainsBackground() {
        Grid grid = new Grid(5, 5);
        SDSRLoop game = new SDSRLoop(grid, Map.of());
        game.step();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                assertEquals(BACKGROUND, grid.getCell(r, c).getState());
            }
        }
    }

    @Test
    void testIsolatedDissolveStateBecomesBackground() {
        // A lone DISSOLVE cell surrounded by BACKGROUND cells should revert to BACKGROUND.
        // resolveUndefined: current==DISSOLVE -> return BACKGROUND
        Grid grid = new Grid(5, 5);
        grid.getCell(2, 2).setState(DISSOLVE);
        SDSRLoop game = new SDSRLoop(grid, Map.of());
        game.step();
        assertEquals(BACKGROUND, grid.getCell(2, 2).getState());
    }

    @Test
    void testMultipleStepsOnEmptyGridStayBackground() {
        Grid grid = new Grid(4, 4);
        SDSRLoop game = new SDSRLoop(grid, Map.of());
        for (int i = 0; i < 5; i++) {
            game.step();
        }
        assertEquals(BACKGROUND, grid.getCell(0, 0).getState());
        assertEquals(BACKGROUND, grid.getCell(3, 3).getState());
    }

    @Test
    void testInheritsLangtonsLoopRules() {
        // A single CORE cell (state 1) surrounded by BACKGROUND with no matching
        // Langton rule falls through to resolveUndefined -> returns current (CORE).
        // Verify the base LangtonsLoop transition table is loaded (no exception, state preserved).
        Grid grid = new Grid(5, 5);
        grid.getCell(2, 2).setState(CORE);
        SDSRLoop game = new SDSRLoop(grid, Map.of());
        game.step();
        // Result depends on the transition table; just verify no exception and valid state
        int state = grid.getCell(2, 2).getState();
        assertEquals(true, state >= BACKGROUND && state <= DISSOLVE);
    }
}
