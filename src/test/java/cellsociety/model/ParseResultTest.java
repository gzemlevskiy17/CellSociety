package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParseResultTest {

    private ParseResult result;
    private int[][] cells;
    private Map<String, String> params;
    private Map<Integer, String> colorMap;

    @BeforeEach
    void setUp() {
        cells = new int[][]{{0, 1}, {2, 3}};
        params = new LinkedHashMap<>();
        params.put("speed", "2.0");
        params.put("neighborhood", "Moore");
        colorMap = new LinkedHashMap<>();
        colorMap.put(0, "#FFFFFF");
        colorMap.put(1, "#000000");
        result = new ParseResult(5, 4, "GameOfLife", "Title", "Author", "Desc",
                cells, params, colorMap);
    }

    @Test
    void testGetRows() {
        assertEquals(5, result.getRows());
    }

    @Test
    void testGetCols() {
        assertEquals(4, result.getCols());
    }

    @Test
    void testGetGameType() {
        assertEquals("GameOfLife", result.getGameType());
    }

    @Test
    void testGetTitle() {
        assertEquals("Title", result.getTitle());
    }

    @Test
    void testGetAuthor() {
        assertEquals("Author", result.getAuthor());
    }

    @Test
    void testGetDescription() {
        assertEquals("Desc", result.getDescription());
    }

    @Test
    void testGetCellsReturnsDefensiveCopy() {
        int[][] copy = result.getCells();
        copy[0][0] = 99;
        int[][] secondCopy = result.getCells();
        assertEquals(0, secondCopy[0][0]);
    }

    @Test
    void testGetCellsCopyIsDistinctObject() {
        assertNotSame(result.getCells(), result.getCells());
    }

    @Test
    void testGetCellsNullWhenNoCells() {
        ParseResult noCell = new ParseResult(2, 2, "GameOfLife", "T", "A", "D",
                null, params, colorMap);
        assertNull(noCell.getCells());
    }

    @Test
    void testGetParamsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> result.getParams().put("newKey", "newVal"));
    }

    @Test
    void testGetColorMapIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> result.getColorMap().put(99, "#FF0000"));
    }

    @Test
    void testGetParamReturnsValue() {
        assertEquals("2.0", result.getParam("speed"));
    }

    @Test
    void testGetNeighborhoodFromParams() {
        assertEquals("Moore", result.getNeighborhood());
    }

    @Test
    void testGetNeighborhoodDefaultsToMoore() {
        ParseResult noNeighborhood = new ParseResult(2, 2, "GameOfLife", "T", "A", "D",
                null, new LinkedHashMap<>(), colorMap);
        assertEquals("Moore", noNeighborhood.getNeighborhood());
    }

    @Test
    void testGetEdgeDefaultsToBounded() {
        assertEquals("Bounded", result.getEdge());
    }

    @Test
    void testGetShapeDefaultsToRectangle() {
        assertEquals("Rectangle", result.getShape());
    }

    @Test
    void testGetColorKeys() {
        assertEquals(2, result.getColorKeys().size());
    }

    @Test
    void testGetColor() {
        assertEquals("#FFFFFF", result.getColor(0));
        assertEquals("#000000", result.getColor(1));
    }
}
