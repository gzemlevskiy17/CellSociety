package cellsociety;

import cellsociety.model.games.ForestFire;
import cellsociety.model.Game;
import cellsociety.model.games.GameOfLife;
import cellsociety.model.ModelFactory;
import cellsociety.model.ParseResult;
import cellsociety.view.ViewFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

class GameFactoryTest {

    @Test
    void createGameOfLifeReturnsCorrectType() {
        ParseResult result = new ParseResult(5, 5, "GameOfLife", "GameOfLife", "Author", "Description", null,
                new HashMap<>(), new HashMap<>());
        ModelFactory factory = new ModelFactory();
        Game game = factory.createGame(result);

        assertInstanceOf(GameOfLife.class, game);
    }

    @Test
    void createForestFireReturnsCorrectType() {
        ParseResult result = new ParseResult(5, 5, "ForestFire", "ForestFire", "Author", "Description", null,
                new HashMap<>(), new HashMap<>());
        ModelFactory factory = new ModelFactory();
        Game game = factory.createGame(result);

        assertInstanceOf(ForestFire.class, game);
    }

    @Test
    void createGameWithInitialCellsPopulates() {
        int[][] initialCells = {
                { 0, 1, 0 },
                { 1, 0, 1 },
                { 0, 1, 0 }
        };
        ParseResult result = new ParseResult(3, 3, "GameOfLife", "GameOfLife", "Author", "Description", initialCells,
                new HashMap<>(), new HashMap<>());
        ModelFactory factory = new ModelFactory();
        Game game = factory.createGame(result);

        assertEquals(0, game.getCell(0, 0).getState());
        assertEquals(1, game.getCell(0, 1).getState());
        assertEquals(0, game.getCell(0, 2).getState());
        assertEquals(1, game.getCell(1, 0).getState());
        assertEquals(0, game.getCell(1, 1).getState());
        assertEquals(1, game.getCell(1, 2).getState());
        assertEquals(0, game.getCell(2, 0).getState());
        assertEquals(1, game.getCell(2, 1).getState());
        assertEquals(0, game.getCell(2, 2).getState());
    }

    @Test
    void createGameWithUnknownTypeThrows() {
        ParseResult result = new ParseResult(5, 5, "UnknownGame", "UnknownGame", "Author", "Description", null,
                new HashMap<>(), new HashMap<>());
        ModelFactory factory = new ModelFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.createGame(result));
    }

    @Test
    void createViewWithColorsParsesCorrectly() {
        Map<Integer, String> colorMap = new HashMap<>();
        colorMap.put(0, "#FFFFFF");
        colorMap.put(1, "#2ECC71");
        ParseResult result = new ParseResult(3, 3, "GameOfLife", "GameOfLife", "Author", "Description", null,
                new HashMap<>(), colorMap);
        ViewFactory factory = new ViewFactory();

        cellsociety.view.GridView view = factory.createView(result);
        assertNotNull(view);
        assertEquals(3, view.getNumRows());
    }
}
