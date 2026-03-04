package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridReaderTest {

    private Grid grid;
    private GridReader reader;

    @BeforeEach
    void setUp() {
        grid = new Grid(3, 4);
        reader = new GridReader(grid);
    }

    @Test
    void testGetRows() {
        assertEquals(3, reader.getRows());
    }

    @Test
    void testGetCols() {
        assertEquals(4, reader.getCols());
    }

    @Test
    void testGetCellStateDefaultZero() {
        assertEquals(0, reader.getCellState(0, 0));
    }

    @Test
    void testGetCellStateReflectsGridState() {
        grid.getCell(1, 2).setState(5);
        assertEquals(5, reader.getCellState(1, 2));
    }

    @Test
    void testGetCellStateAfterUpdate() {
        grid.getCell(2, 3).setState(1);
        grid.getCell(2, 3).setNextState(3);
        grid.getCell(2, 3).updateState();
        assertEquals(3, reader.getCellState(2, 3));
    }

    @Test
    void testReaderReflectsAllCells() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                grid.getCell(r, c).setState(r + c);
            }
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                assertEquals(r + c, reader.getCellState(r, c));
            }
        }
    }
}
