package cellsociety.controller;

import cellsociety.model.ParseResult;
import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.XMLParser;
import cellsociety.view.GridView;
import cellsociety.view.LanguageView;
import cellsociety.view.MainLauncher;
import cellsociety.view.ScreenManager;
import cellsociety.view.ViewFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;

import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

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
    // in contructor also calls the language screen
    Optional<File> optionalFile = screenManager.startScreen(myStage);
    optionalFile.ifPresent(file -> handleRealLoad(file));
  }

  private void handleRealLoad(File file) {
    ParseResult parseResult;
    try {
      parseResult = XMLParser.parse(file);
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
    controller = new Controller(game, screenManager, gridView, currentParseResult.getSpeed(), createNewGameAction);
    controller.gameScreenShow(this::handleSave, this::setup, createNewGameAction, this::loadForResults);
    controller.reset();
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
        currentParseResult.getColorMap());
    try {
      XMLParser.save(snapshot, myStage);
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }
}
