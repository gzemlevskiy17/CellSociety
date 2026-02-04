package cellsociety;

import cellsociety.model.ForestFire;
import cellsociety.model.Game;
import cellsociety.model.GameOfLife;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GameFactoryTest {

    @Test
    void createGameOfLifeReturnsCorrectType() {
        ParseResult result = new ParseResult(5, 5, "GameOfLife", null, 0.0, 0.0);
        GameFactory factory = new GameFactory();
        Game game = factory.createGame(result);

        assertInstanceOf(GameOfLife.class, game);
    }

    @Test
    void createForestFireReturnsCorrectType() {
        ParseResult result = new ParseResult(5, 5, "ForestFire", null, 0.6, 0.02);
        GameFactory factory = new GameFactory();
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
        ParseResult result = new ParseResult(3, 3, "GameOfLife", initialCells, 0.0, 0.0);
        GameFactory factory = new GameFactory();
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
        ParseResult result = new ParseResult(5, 5, "UnknownGame", null, 0.0, 0.0);
        GameFactory factory = new GameFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.createGame(result));
    }
}
