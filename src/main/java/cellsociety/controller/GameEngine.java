package cellsociety.controller;

import cellsociety.GameFactory;
import cellsociety.ParseResult;
import cellsociety.model.Game;
import cellsociety.model.XMLParser;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import java.io.File;
import java.io.IOException;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class GameEngine {
  public static final String DATA_PATH = "cellsociety_team01/data/";

  private Stage primaryStage;
  private ScreenManager screenManager;
  private GameFactory gameFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;
  private ParseResult currentParseResult;

  public GameEngine(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.show();
    setup(primaryStage);
  }

  private void setup(Stage primaryStage) {
    gameFactory = new GameFactory();
    screenManager = new ScreenManager(primaryStage, gridView); // in contructor also calls the language screen
    File initialStart = screenManager.startScreen(primaryStage);
    if (initialStart == null)
      return;
    ParseResult parseResult = null;
    try {
      parseResult = XMLParser.parse(initialStart);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
    currentParseResult = parseResult;
    game = gameFactory.createGame(parseResult);
    gridView = gameFactory.createView(parseResult);
    controller = new Controller(game, screenManager, gridView);
    controller.gameScreenShow(this::handleSave, this::handleLoad);
    controller.loop();
  }

  private void handleSave() {
    controller.pause();
    int[][] currentCells = game.getGrid().getCellStates();
    ParseResult snapshot = new ParseResult(
        currentParseResult.getRows(),
        currentParseResult.getCols(),
        currentParseResult.getGameType(),
        currentParseResult.getTitle(),
        currentParseResult.getAuthor(),
        currentParseResult.getDescription(),
        currentCells,
        currentParseResult.getProbCatch(),
        currentParseResult.getProbGrow(),
        currentParseResult.getColorMap());
    try {
      XMLParser.save(snapshot, primaryStage);
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleLoad() {
    controller.pause();
    File file = screenManager.startScreen(primaryStage);
    if (file == null) {
      return;
    }
    try {
      ParseResult parseResult = XMLParser.parse(file);
      currentParseResult = parseResult;
      game = gameFactory.createGame(parseResult);
      gridView = gameFactory.createView(parseResult);
      screenManager.setGridView(gridView);
      controller = new Controller(game, screenManager, gridView);
      controller.play();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleKeyInput(KeyCode code) {
  }
}
