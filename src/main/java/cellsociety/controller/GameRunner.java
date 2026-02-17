package cellsociety.controller;

import cellsociety.ParseResult;
import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.XMLParser;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import cellsociety.view.ViewFactory;
import java.io.File;
import java.io.IOException;
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

  private Runnable createNewGameAction;

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
    // in contructor also calls the language screen
    Optional<File> optionalFile = screenManager.startScreen(primaryStage);
    optionalFile.ifPresent(file -> handleRealLoad(file));
  }

  private void handleRealLoad(File file) {
    ParseResult parseResult = null;
    try {
      parseResult = XMLParser.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
    currentParseResult = parseResult;
    game = modelFactory.createGame(parseResult);
    gridView = viewFactory.createView(parseResult);
    controller = new Controller(game, screenManager, gridView, parseResult.getSpeed(), createNewGameAction);
    controller.gameScreenShow(this::handleSave, this::handleLoad, createNewGameAction);
    controller.loop();
  }

  private void handleSave() {
    controller.pause();
    int[][] currentCells = game.getGrid().getCellStates();
    double currentSpeed = controller.getGameSpeed();
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
        currentSpeed,
        currentParseResult.getThreshold(),
        currentParseResult.getColorMap());
    try {
      XMLParser.save(snapshot, myStage);
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleLoad() {
    setup(myStage);
  }
}
