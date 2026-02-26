package cellsociety.controller;

import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.ParseResult;
import cellsociety.model.XMLReader;
import cellsociety.model.XMLWriter;
import cellsociety.view.ErrorView;
import cellsociety.view.GridView;
import cellsociety.view.MainLauncher;
import cellsociety.view.ScreenManager;
import cellsociety.view.ViewFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;
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
 * Dependencies: ModelFactory, ViewFactory, XMLParser, ScreenManager,
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
  private ScreenManager screenManager;
  private ModelFactory modelFactory;
  private ViewFactory viewFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;
  private ParseResult currentParseResult;
  private MainLauncher startChoice;

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
    startChoice = new MainLauncher(myStage, this::firstStart);
  }

  private void firstStart() {
    screenManager = new ScreenManager(myStage, startChoice.getLanguage());
    setup();
  }

  private void setup() {
    myStage.show();
    Optional<File> optionalFile = screenManager.startScreen(myStage);
    optionalFile.ifPresent(file -> {
      try {
        handleRealLoad(file);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void handleRealLoad(File file) throws Exception {
    ParseResult parseResult;
    try {
      parseResult = XMLReader.parse(file);
      currentParseResult = parseResult;
    } catch (Exception e) {
      new ErrorView("Configuration Error", e.getMessage());
      throw new Exception();
    }
    try {
      loadForResults();
    } catch (IllegalArgumentException e) {
      new ErrorView("Wrong Arguments", e.getMessage());
      throw new Exception();
    }
  }

  private void loadForResults() throws IllegalArgumentException {
    game = modelFactory.createGame(currentParseResult);
    gridView = viewFactory.createView(currentParseResult);
    controller = new Controller(game, screenManager, gridView, currentParseResult,
        createNewGameAction);
    controller.gameScreenShow(this::handleSave, this::setup, createNewGameAction, this::loadForResults);
    controller.reset();
  }

  private void handleSave() {
    controller.pause();
    int[][] currentCells = game.getGrid().getCellStates();
    updateParams("speed", controller.getGameSpeed() + "");
    ParseResult snapshot = new ParseResult(
        currentParseResult.getRows(),
        currentParseResult.getCols(),
        currentParseResult.getGameType(),
        currentParseResult.getTitle(),
        currentParseResult.getAuthor(),
        currentParseResult.getDescription(),
        currentCells,
        currentParseResult.getParams(),
        currentParseResult.getColorMap());
    try {
      XMLWriter.save(snapshot, myStage);
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void updateParams(String name, String newValue) {
    Map<String, String> toBeChanged = currentParseResult.getParams();
    toBeChanged.put(name, newValue);
  }
}
