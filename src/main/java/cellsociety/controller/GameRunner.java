package cellsociety.controller;

import cellsociety.model.ParseResult;
import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.XMLReader;
import cellsociety.model.XMLWriter;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import cellsociety.view.ViewFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;

import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Orchestrates application flow for a single window: shows start screen, loads XML, constructs
 * model/view/controller, and wires save/load actions.
 *
 * <p>Purpose: Manage one application's lifecycle in a single JavaFX Stage. It is
 * responsible for reading a configuration file, creating the Game and GridView via factories, and
 * instantiating Controller to drive the simulation.</p>
 *
 * <p>Assumptions: The provided Stage is valid and JavaFX runtime is initialized.
 * XML configuration files must be valid for the ModelFactory and XMLParser to succeed.</p>
 *
 * <p>Dependencies: ModelFactory, ViewFactory, XMLParser, ScreenManager, Controller.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * new GameRunner(primaryStage, () -> { /* create new window *\/ });
 * }</pre>
 *
 * @author Georgiy Zemlevskiy
 * @author Michael Li
 */
public class GameRunner {

  private Stage myStage;
  private ScreenManager screenManager;
  private ModelFactory modelFactory;
  private ViewFactory viewFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;
  private ParseResult currentParseResult;

  private Runnable createNewGameAction;

  /**
   * Creates a GameRunner that drives the application flow on the provided stage. Shows the start
   * screen and, if a configuration file is chosen, loads it.
   *
   * @param myStage             the primary JavaFX stage to use for UI
   * @param createNewGameAction action invoked when a new game window should be created
   */
  public GameRunner(Stage myStage, Runnable createNewGameAction) {
    this.myStage = myStage;
    this.createNewGameAction = createNewGameAction;
    modelFactory = new ModelFactory();
    viewFactory = new ViewFactory();
    screenManager = new ScreenManager(myStage);
    setup(myStage);
    myStage.show();
  }

  private void setup(Stage primaryStage) {
    Optional<File> optionalFile = screenManager.startScreen(primaryStage);
    optionalFile.ifPresent(file -> handleRealLoad(file));
  }

  private void handleRealLoad(File file) {
    ParseResult parseResult;
    try {
      parseResult = XMLReader.parse(file);
      currentParseResult = parseResult;
    } catch (Exception e) {
      screenManager.showError("Configuration Error", e.getMessage());
      return;
    }
    loadForResults();
  }

  private void loadForResults() {
    game = modelFactory.createGame(currentParseResult);
    gridView = viewFactory.createView(currentParseResult);
    controller = new Controller(game, screenManager, gridView, currentParseResult.getSpeed(),
        createNewGameAction);
    controller.gameScreenShow(this::handleSave, this::handleLoad, createNewGameAction,
        this::loadForResults);
    controller.updateGrid();
  }

  private void handleSave() {
    controller.pause();
    int[][] currentCells = game.getGrid().getCellStates();
    double currentSpeed = controller.getGameSpeed();
    Map<String, Double> toBeChanged = currentParseResult.getParams();
    toBeChanged.replace("speed", Double.valueOf(currentSpeed));
    ParseResult snapshot = new ParseResult(
        currentParseResult.getRows(),
        currentParseResult.getCols(),
        currentParseResult.getGameType(),
        currentParseResult.getTitle(),
        currentParseResult.getAuthor(),
        currentParseResult.getDescription(),
        currentCells,
        toBeChanged,
        currentParseResult.getColorMap(),
        currentParseResult.getRulestring());
    try {
      XMLWriter.save(snapshot, myStage);
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleLoad() {
    setup(myStage);
  }
}
