package cellsociety.model;

import cellsociety.model.topology.BoundedEdge;
import cellsociety.model.topology.EdgePolicy;
import cellsociety.model.topology.ExtendedMooreNeighborhood;
import cellsociety.model.topology.HexagonalNeighborhood;
import cellsociety.model.topology.MirrorEdge;
import cellsociety.model.topology.MooreNeighborhood;
import cellsociety.model.topology.NeighborhoodStrategy;
import cellsociety.model.topology.ToroidalEdge;
import cellsociety.model.topology.TriangularNeighborhood;
import cellsociety.model.topology.VonNeumannNeighborhood;
import java.lang.reflect.Constructor;
import java.util.Map;

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
      Class<?> gameClazz = Class.forName("cellsociety.model.games." + gameType);
      if (!gameClazz.getSuperclass().equals(superClazzReq)) {
        throw new IllegalArgumentException("Unknown game type: " + gameType);
      }
      Class<?>[] paramTypes = new Class<?>[] { Grid.class, Map.class };
      try {
        Constructor<?> gameCalled = gameClazz.getDeclaredConstructor(paramTypes);
        return (Game) gameCalled.newInstance(new Object[] { grid, result.getParams() });
      } catch (Exception e) {
        throw new IllegalArgumentException(gameType + " constructor could not be invoked", e);
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unknown game type: " + gameType);
    }
  }

  private NeighborhoodStrategy buildNeighborhood(String name) {
    return switch (name) {
      case "VonNeumann" -> new VonNeumannNeighborhood();
      case "ExtendedMoore" -> new ExtendedMooreNeighborhood();
      case "Hexagonal" -> new HexagonalNeighborhood();
      case "Triangular" -> new TriangularNeighborhood();
      default -> new MooreNeighborhood(); // "Moore" and any unrecognized
    };
  }

  private EdgePolicy buildEdge(String name) {
    return switch (name) {
      case "Toroidal" -> new ToroidalEdge();
      case "Mirror" -> new MirrorEdge();
      default -> new BoundedEdge(); // "Bounded" and any unrecognized
    };
  }
}
