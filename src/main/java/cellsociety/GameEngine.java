package cellsociety;

import cellsociety.model.Game;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import java.io.IOException;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class GameEngine {
  public static final String DATA_PATH = "resources/";

  private ScreenManager screenManager;
  private GameFactory gameFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;

  public GameEngine(Stage primaryStage) {
    setup(primaryStage);
    // setupView();
  }

  private void setup(Stage primaryStage) {
    gameFactory = new GameFactory();
    ParseResult parseResult = null;
    try {
      parseResult = XMLParser.parse(GameEngine.class.getResourceAsStream("/GameOfLifeSimple.xml"));
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    game = gameFactory.createGame(parseResult);
    gridView = gameFactory.createView(parseResult);
    screenManager = new ScreenManager(primaryStage, gridView);
    controller = new Controller(game, screenManager, gridView);
    controller.play();
    controller.loop();
  }

  private void setupView() {

  }

  private void handleKeyInput(KeyCode code) {
  }
}
