package cellsociety.controller;

import cellsociety.model.Game;
import cellsociety.model.GridReader;
import cellsociety.model.ModelFactory;
import cellsociety.model.ParseResult;
import cellsociety.model.XMLReader;
import cellsociety.model.XMLWriter;
import cellsociety.view.ErrorView;
import cellsociety.view.GameScreen;
import cellsociety.view.StartScreen;
import cellsociety.view.ViewFactory;
import cellsociety.view.grids.GridView;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Orchestrates application flow for a single window: shows start screen, loads
 * XML, constructs
 * model/view/controller, and wires save/load actions.
 *
 * <p>
 * Purpose: Manage one application's lifecycle in a single JavaFX Stage. It is
 * responsible for reading a configuration file, creating the Game and GridView
 * via factories, and
 * instantiating Controller to drive the simulation.
 * </p>
 *
 * <p>
 * Assumptions: The provided Stage is valid and JavaFX runtime is initialized.
 * XML configuration files must be valid for the ModelFactory and XMLParser to
 * succeed.
 * </p>
 *
 * <p>
 * Dependencies: ModelFactory, ViewFactory, XMLParser, GameScreen,
 * Controller.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * new GameRunner(primaryStage, () -> { /* create new window *\/ });
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 * @author Michael Li
 */
public class GameRunner {

  private Stage myStage;
  private GameScreen gameScreen;
  private ModelFactory modelFactory;
  private ViewFactory viewFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;
  private ParseResult currentParseResult;
  private StartScreen startScreen;

  private Runnable createNewGameAction;

  /**
   * Creates a GameRunner that drives the application flow on the provided stage.
   * Shows the start
   * screen and, if a configuration file is chosen, loads it.
   *
   * @param myStage             the primary JavaFX stage to use for UI
   * @param createNewGameAction action invoked when a new game window should be
   *                            created
   */
  public GameRunner(Stage myStage, Runnable createNewGameAction) {
    this.myStage = myStage;
    this.createNewGameAction = createNewGameAction;
    modelFactory = new ModelFactory();
    viewFactory = new ViewFactory();
    startScreen = new StartScreen(myStage, this::firstStart);
  }

  private void firstStart() {
    gameScreen = new GameScreen(myStage, startScreen.getLocale());
    setup();
  }

  private void setup() {
    myStage.show();
    Optional<File> optionalFile = gameScreen.startScreen(myStage);
    optionalFile.ifPresent(file -> {
      try {
        handleRealLoad(file);
      } catch (IllegalArgumentException e) {
        new ErrorView("Configuration Error", e.getMessage());
      } catch (Exception e) {
        new ErrorView("Unexpected Error", "Failed to load simulation: " + e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private void handleRealLoad(File file) throws IllegalArgumentException {
    ParseResult parseResult;
    try {
      parseResult = XMLReader.parse(file);
      currentParseResult = parseResult;
    } catch (Exception e) {
      new ErrorView("Configuration Error", e.getMessage());
      throw new IllegalArgumentException("Failed to parse XML file: " + file.getName(), e);
    }
    try {
      loadForResults();
    } catch (IllegalArgumentException e) {
      new ErrorView("Wrong Arguments", e.getMessage());
      throw new IllegalArgumentException("Failed to create game from configuration", e);
    }
  }

  private void loadForResults() throws IllegalArgumentException {
    game = modelFactory.createGame(currentParseResult);
    gridView = viewFactory.createView(currentParseResult);
    controller = new Controller(game, gameScreen, gridView, currentParseResult,
        createNewGameAction);
    controller.gameScreenShow(currentParseResult, this::handleSave, this::setup, createNewGameAction,
        this::loadForResults, this::toBeUpdates);
    controller.reset();
  }

  private void handleSave() {
    controller.pause();
    GridReader reader = game.getGridReader();
    int[][] currentCells = new int[reader.getRows()][reader.getCols()];
    for (int r = 0; r < reader.getRows(); r++) {
      for (int c = 0; c < reader.getCols(); c++) {
        currentCells[r][c] = reader.getCellState(r, c);
      }
    }
    ParseResult snapshot = getSnapshot(currentCells);
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Simulation As");
    File dataDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data").toString());
    if (dataDir.exists()) {
      fileChooser.setInitialDirectory(dataDir);
    }
    fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    fileChooser.setInitialFileName("simulation.xml");
    File outputFile = fileChooser.showSaveDialog(myStage);
    if (outputFile == null) {
      return;
    }
    try {
      XMLWriter.save(snapshot, outputFile);
    } catch (ParserConfigurationException e) {
      new ErrorView("Save Error", "Failed to configure XML writer: " + e.getMessage());
    } catch (TransformerException e) {
      new ErrorView("Save Error", "Failed to write XML file: " + e.getMessage());
    }
  }

  private ParseResult getSnapshot(int[][] currentCells) {
    Map<String, String> currentParams = currentParseResult.getParams();
    currentParams.put("speed", controller.getGameSpeed() + "");
    return new ParseResult(
        currentParseResult.getRows(),
        currentParseResult.getCols(),
        currentParseResult.getGameType(),
        currentParseResult.getTitle(),
        currentParseResult.getAuthor(),
        currentParseResult.getDescription(),
        currentCells,
        currentParams,
        currentParseResult.getColorMap());
  }

  private void toBeUpdates() {
    currentParseResult = gameScreen.getResultChanges();
    controller.toBeUpdates(currentParseResult);
  }
}
