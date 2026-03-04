package cellsociety.model;

import cellsociety.model.topology.edgebehavior.EdgePolicy;
import cellsociety.model.topology.neighborhood.NeighborhoodStrategy;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * ParseResult result = XMLReader.parse(new File("data/GameOfLife/GameOfLifeSimple.xml"));
 * ModelFactory factory = new ModelFactory();
 * Game game = factory.createGame(result);
 * game.step();
 * }</pre>
 *
 * @author Michael Li
 */
public class ModelFactory {

  private static final Logger LOG = LogManager.getLogger(ModelFactory.class);
  private static final String GAMES_PACKAGE = "cellsociety.model.games.";
  private static final String NEIGHBORHOOD_PACKAGE = "cellsociety.model.topology.neighborhood.";
  private static final String EDGE_PACKAGE = "cellsociety.model.topology.edgebehavior.";

  /**
   * Creates a {@link Game} instance matching the simulation type in the given
   * result.
   * Builds and populates the grid from the cell data if present.
   *
   * @param result the parsed XML configuration
   * @return a new {@link Game} subclass instance ready to run
   * @throws IllegalArgumentException if the game type is not recognized
   */
  public Game createGame(ParseResult result) throws IllegalArgumentException {
    int rows = result.getRows();
    int cols = result.getCols();
    String gameType = result.getGameType();
    int[][] cells = result.getCells();

    NeighborhoodStrategy neighborhood = buildNeighborhood(result.getNeighborhood());
    EdgePolicy edge = buildEdge(result.getEdge());
    Grid grid = new Grid(rows, cols, cells, neighborhood, edge);

    Class<?> superClazzReq = Game.class;
    try {
      Class<?> gameClazz = Class.forName(GAMES_PACKAGE + gameType);
      if (!superClazzReq.isAssignableFrom(gameClazz)) {
        throw new IllegalArgumentException("Unknown game type: " + gameType);
      }
      Class<?>[] gameParamTypes = new Class<?>[]{Grid.class, Map.class};
      try {
        Constructor<?> gameConstructor = gameClazz.getDeclaredConstructor(gameParamTypes);
        return (Game) gameConstructor.newInstance(new Object[]{grid, result.getParams()});
      } catch (Exception e) {
        throw new IllegalArgumentException(gameType + " constructor could not be invoked", e);
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unknown game type: " + gameType);
    }
  }

  private NeighborhoodStrategy buildNeighborhood(String neighborhood) {
    //TODO: store neighborhood types in properties file and handle unrecognized neighborhood types
    return buildFromReflection(
        NEIGHBORHOOD_PACKAGE, neighborhood, "Neighborhood",
        NeighborhoodStrategy.class);
  }

  private EdgePolicy buildEdge(String edgeType) {
    //TODO: store edge types in properties file and handle unrecognized edge types
    return buildFromReflection(
        EDGE_PACKAGE, edgeType, "Edge",
        EdgePolicy.class);
  }

  private <T> T buildFromReflection(String basePackage, String name, String suffix,
      Class<T> expectedType) {
    try {
      Object instance = Class.forName(basePackage + name + suffix)
          .getDeclaredConstructor().newInstance();
      return expectedType.cast(instance);
    } catch (Exception e) {
      String message = name + " constructor could not be invoked";
      LOG.warn(message, e);
      throw new IllegalArgumentException(message, e);
    }
  }
}
