package cellsociety;

import cellsociety.model.ForestFire;
import cellsociety.model.Game;
import cellsociety.model.GameOfLife;
import cellsociety.model.Grid;

public class GameFactory {
    public Game createGame(ParseResult result) {
        int rows = result.getRows();
        int cols = result.getCols();

        String gameType = result.getGameType();
        int[][] cells = result.getCells();

        Grid grid = new Grid(rows, cols);

        if (cells != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid.getCell(i, j).setState(cells[i][j]);
                }
            }
        }

        Game game = switch (gameType) {
            case "GameOfLife" -> new GameOfLife(grid);
            case "ForestFire" -> new ForestFire(grid, result.getProbCatch(), result.getProbGrow());
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };

        return game;

    }
}
