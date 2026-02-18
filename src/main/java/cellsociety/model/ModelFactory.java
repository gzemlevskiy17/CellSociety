package cellsociety.model;

/**
 * Factory that creates {@link Game} instances from parsed XML configuration
 * data.
 *
 * <p>
 * Builds a {@link Grid}, populates it with initial cell states from the
 * {@link ParseResult}, and instantiates the correct {@link Game} subclass based
 * on the simulation type string.
 * </p>
 *
 * <p>
 * <b>Assumptions:</b> The {@code gameType} in the {@link ParseResult} must
 * match
 * one of the supported types ("GameOfLife", "ForestFire", "Segregation"). An
 * unknown
 * type throws {@link IllegalArgumentException}.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link ParseResult}, {@link Grid}, {@link GameOfLife},
 * {@link ForestFire}, {@link Segregation}
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * ParseResult result = XMLReader.parse(new File("data/GameOfLifeSimple.xml"));
 * ModelFactory factory = new ModelFactory();
 * Game game = factory.createGame(result);
 * game.step();
 * }</pre>
 *
 * @author Michael Li
 */
public class ModelFactory {

  /**
   * Creates a {@link Game} instance matching the simulation type in the given
   * result.
   * Builds and populates the grid from the cell data if present.
   *
   * @param result the parsed XML configuration
   * @return a new {@link Game} subclass instance ready to run
   * @throws IllegalArgumentException if the game type is not recognized
   */
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
      case "ForestFire" ->
        new ForestFire(grid, result.getAParam("ignitionProbability"), result.getAParam("fillProbability"));
      case "Segregation" -> new Segregation(grid, result.getAParam("threshold"));
      case "WaTor" -> new WaTor(grid, result.getParams());
      case "Percolation" -> new Percolation(grid);
      default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
    };
  }
}
