package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

class XMLReaderTest {

    @Test
    void parseInvalidXmlThrows() {
        File file = new File("data/Error/BadData.xml");
        assertThrows(IllegalArgumentException.class, () -> XMLReader.parse(file));
    }

    @Test
    void parseEmptyFileThrows() {
        File file = new File("data/Error/ErrorEmptyFile.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertTrue(ex.getMessage().contains("File is empty"));
    }

    @Test
    void parseNotXmlFileThrows() {
        File file = new File("data/Error/ErrorNotXml.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertTrue(ex.getMessage().contains("not valid XML"));
    }

    @Test
    void testParseActualFile() throws Exception {
        File file = new File("data/GameOfLife/GameOfLifeSimple.xml");
        ParseResult result = XMLReader.parse(file);

        assertEquals("GameOfLife", result.getGameType());
        assertEquals(3, result.getRows());
        assertEquals(3, result.getCols());

        // Verify the specific cell at (1,1) is 1 (ALIVE)
        int[][] cells = result.getCells();
        assertEquals(1, cells[1][1]);
        assertEquals(0, cells[0][0]);

        // Verify colors are parsed
        java.util.Map<Integer, String> colors = result.getColorMap();
        assertEquals("#FFFFFF", colors.get(0));
        assertEquals("#2ECC71", colors.get(1));
    }

    @Test
    void parseStateDistributionByCount() throws Exception {
        File file = new File("data/GameOfLife/GameOfLifeRandom.xml");
        ParseResult result = XMLReader.parse(file);

        assertEquals("GameOfLife", result.getGameType());
        assertEquals(10, result.getRows());
        assertEquals(10, result.getCols());

        int[][] cells = result.getCells();
        assertNotNull(cells);

        int aliveCount = 0;
        for (int[] row : cells) {
            for (int cell : row) {
                if (cell == 1)
                    aliveCount++;
            }
        }
        assertEquals(30, aliveCount);
    }

    @Test
    void parseStateDistributionByProportion() throws Exception {
        File file = new File("data/Segregation/SegregationRandom.xml");
        ParseResult result = XMLReader.parse(file);

        assertEquals("Segregation", result.getGameType());
        assertEquals(10, result.getRows());
        assertEquals(10, result.getCols());

        int[][] cells = result.getCells();
        assertNotNull(cells);

        int[] stateCounts = new int[3];
        for (int[] row : cells) {
            for (int cell : row) {
                stateCounts[cell]++;
            }
        }
        // 30% of 100 = 30 cells for each group
        assertEquals(30, stateCounts[1]);
        assertEquals(30, stateCounts[2]);
        // Remaining 40% = 40 empty
        assertEquals(40, stateCounts[0]);
    }

    @Test
    void parseMissingTypeThrows() {
        File file = new File("data/Error/ErrorMissingType.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertEquals("Missing required field: type", ex.getMessage());
    }

    @Test
    void parseInvalidSimulationTypeThrows() {
        File file = new File("data/Error/ErrorInvalidType.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertEquals("Invalid simulation type: FakeGame", ex.getMessage());
    }

    @Test
    void parseNegativeDimensionThrows() {
        File file = new File("data/Error/ErrorNegativeDimension.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertEquals("Grid dimensions must be positive, got: -3x3", ex.getMessage());
    }

    @Test
    void parseInvalidCellStateThrows() {
        File file = new File("data/Error/ErrorInvalidCellState.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertTrue(ex.getMessage().contains("Invalid cell state 5"));
    }

    @Test
    void parseOutOfBoundsCellsThrows() {
        File file = new File("data/Error/ErrorOutOfBounds.xml");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> XMLReader.parse(file));
        assertTrue(ex.getMessage().contains("Too many rows"));
    }
}
