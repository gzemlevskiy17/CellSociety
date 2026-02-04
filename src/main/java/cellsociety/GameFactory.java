package cellsociety;

import cellsociety.model.ForestFire;
import cellsociety.model.Game;
import cellsociety.model.GameOfLife;
import cellsociety.model.Grid;

import cellsociety.view.GridView;
import cellsociety.view.RectangularGridView;
import javafx.scene.paint.Color;
import java.util.HashMap;

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

    public GridView createView(ParseResult result) {
        int rows = result.getRows();
        int cols = result.getCols();
        String gameType = result.getGameType();

        HashMap<Integer, Color> colorMap = new HashMap<>();
        java.util.Map<Integer, String> parsedColors = result.getColorMap();

        if (parsedColors != null && !parsedColors.isEmpty()) {
            for (Integer id : parsedColors.keySet()) {
                colorMap.put(id, Color.web(parsedColors.get(id)));
            }
        } else {
            colorMap.put(0, Color.WHITE);
            colorMap.put(1, Color.BLACK);
            colorMap.put(2, Color.RED);
        }

        return new RectangularGridView(gameType, colorMap, cols, rows);
    }
}
