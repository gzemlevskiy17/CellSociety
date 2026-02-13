package cellsociety.model;

import cellsociety.ParseResult;

public class ModelFactory {

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

    return switch (gameType) {
      case "GameOfLife" -> new GameOfLife(grid);
      case "ForestFire" -> new ForestFire(grid, result.getProbCatch(), result.getProbGrow());
      case "Segregation" -> new Segregation(grid, result.getProbCatch());
      default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
    };
  }
}
