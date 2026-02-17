package cellsociety.view;

import cellsociety.controller.Controller;
import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.ParseResult;
import cellsociety.model.XMLParser;
import java.io.File;
import java.io.IOException;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class SimulationScreenTest extends DukeApplicationTest {

  public static final String DATA_PATH = "data/";

  private ScreenManager screenManager;
  private ModelFactory modelFactory;
  private ViewFactory viewFactory;
  private Game game;
  private Controller controller;
  private GridView gridView;

  @Override
  public void start(Stage primaryStage) {
    modelFactory = new ModelFactory();
    viewFactory = new ViewFactory();
    ParseResult parseResult = null;
    try {
      parseResult = XMLParser.parse(new File(DATA_PATH + "GameOfLifeSimple.xml"));
    } catch (ParserConfigurationException | IOException e) {
      throw new RuntimeException(e);
    }
    game = modelFactory.createGame(parseResult);
    gridView = viewFactory.createView(parseResult);
    // screenManager = new ScreenManager(primaryStage, gridView);
    // controller = new Controller(game, screenManager, gridView);
    // controller.play();
    // controller.loop();
  }

  @Test
  void buttonClick_PlayPause_GamePaused() {
  }

  @Test
  void buttonClick_PlayPause_GamePlaying() {
  }

  @Test
  void buttonClick_Reset_ResetToOriginalState() {
  }

  @Test
  void buttonClick_Clear_CellsSetToDefaultState() {
  }

  @Test
  void buttonClick_Cell_CycleCellState() {
  }

  @Test
  void slider_MoveRight_GameSpeedIncreased() {
  }

  @Test
  void slider_MoveLeft_GameSpeedDecreased() {
  }

}
