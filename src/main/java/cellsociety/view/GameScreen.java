package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import cellsociety.model.ParseResult;
import cellsociety.view.grids.GridView;
import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the main simulation window layout, including the grid display,
 * playback controls, speed slider, and generation counter.
 *
 * <p>Wires UI events (play, pause, step, reset, save, load) to caller-supplied
 * {@link Runnable} callbacks so that this class remains decoupled from the
 * controller logic.</p>
 *
 * @author cellsociety team
 */

public class GameScreen extends StylableView {
  public static final String THEME_STYLESHEET = "light.css";
  private static final Logger LOG = LogManager.getLogger(GameScreen.class);
  private Stage myStage;
  private GridView gridView;
  private InfoView infoView;
  private ButtonCreator buttons;
  private boolean onPlay;
  private Label dynamicGenerations;
  private Slider speedSlider;
  private int generations;
  private ParseResult parseHolder;
  private ResourceBundle screenText;
  private ResourceBundle config;
  private ResourceBundle buttonsToCreate;
  // TODO: figure out default_resource_folder across multiple files

  public GameScreen(Stage stage) {
    myStage = stage;
    config = PropertiesLoader.getResourceBundle("view/Config/GameScreen/GameScreen");
    buttonsToCreate = PropertiesLoader.getResourceBundle("view/Config/GameScreen/Buttons");
    registerLanguageListener();
  }

  public void checkAllNotNull(Object... params) throws Exception { // TODO: do we need this
    for (Object param : params) {
      if (param == null) {
        throw new NullPointerException("A parameter is null.");
      }
    }
  }

  public void setGridView(GridView newGridView) {
    gridView = newGridView;
  }

  public void gameScreen(ParseResult parsingInfo, Runnable onSave, Runnable onLoad, Runnable pause,
      Runnable play,
      Runnable reset, Runnable setGameSpeed,
      Runnable createNewGameAction, Runnable clear, Runnable stepForward, Runnable stepBack, Runnable pullChanges,
      Runnable toggleLine,
      int startingGenerations,
      double startSpeed) {
    parseHolder = parsingInfo;
    generations = startingGenerations;
    onPlay = true; // TODO: make more elegant
    infoView = new InfoView(myStage, parseHolder, pullChanges);
    buttons = new ButtonCreator();
    String[] names = buttonsToCreate.getString("names").split("[,\s]+"); // TODO: reflection
    String[] info = { "Info", "EditInfo" };
    String[] toggling = { "Outline" };

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    BorderPane titleBox = createTopBox(info);
    mainScreen.setTop(titleBox);

    VBox simulationControl = createControlArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction, clear, stepForward, stepBack, startSpeed);
    mainScreen.setBottom(simulationControl);

    double windowWidth = Double.parseDouble(config.getString("window.width"));
    double windowHeight = Double.parseDouble(config.getString("window.height"));
    VBox toggleControl = createToggleArea(toggling, toggleLine);
    mainScreen.setRight(toggleControl);

    Scene scene = new Scene(mainScreen, windowWidth, windowHeight);

    registerThemeListenerWithLayoutSheet(scene);

    myStage.setOnHidden(event -> unregisterAllListeners());

    String titleOfWindow = config.getString("title") + gridView.getGameName();

    myStage.setTitle(titleOfWindow);
    myStage.setScene(scene);
    myStage.centerOnScreen();
  }

  private BorderPane createTopBox(String[] names) {
    Label topLabel = new Label(gridView.getGameName());
    topLabel.setId("top-label");
    BorderPane titleBox = new BorderPane(topLabel);

    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    buttons.addActionToButton("Info", e -> infoView.showInfoView(parseHolder));
    buttons.addActionToButton("EditInfo", e -> infoView.showInfoEdit(parseHolder));
    VBox infoControl = new VBox();
    infoControl.setId("info-control-box");
    for (String name : names) {
      infoControl.getChildren().add(buttons.getButton(name));
    }
    titleBox.setRight(infoControl);
    return titleBox;
  }

  private VBox createControlArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause,
      Runnable play, Runnable reset, Runnable setGameSpeed, Runnable createNewGameAction,
      Runnable clear, Runnable stepForward, Runnable stepBack,
      double startSpeed) {

    HBox timeArea = createTimeAreaBox(setGameSpeed, startSpeed);

    HBox buttonArea = createButtonBoxArea(names, onSave, onLoad, pause, play,
        reset, createNewGameAction, clear, stepForward, stepBack);

    VBox simulationControl = new VBox();
    simulationControl.setId("simulation-control-box");
    simulationControl.getChildren().addAll(timeArea, buttonArea);
    return simulationControl;
  }

  private HBox createTimeAreaBox(Runnable setGameSpeed, double startSpeed) {
    HBox timeAreaBox = new HBox();
    timeAreaBox.setId("time-area-box");

    double sliderMin = Double.parseDouble(config.getString("slider.min"));
    double sliderMax = Double.parseDouble(config.getString("slider.max"));
    speedSlider = new Slider(sliderMin, sliderMax, startSpeed);

    double sliderPrefWidth = Double.parseDouble(config.getString("slider.prefWidth"));
    double sliderMaxWidth = Double.parseDouble(config.getString("slider.maxWidth"));
    speedSlider.setPrefWidth(sliderPrefWidth);
    speedSlider.setMaxWidth(sliderMaxWidth);
    speedSlider.setShowTickMarks(true); // TODO: need properties?

    double majorTickUnit = Double.parseDouble(config.getString("slider.majorTickUnit"));
    speedSlider.setMajorTickUnit(majorTickUnit);
    speedSlider.setSnapToTicks(true);

    speedSlider.valueProperty().addListener((observable) -> setGameSpeed.run());

    dynamicGenerations = new Label();
    dynamicGenerations.setText("Gen: " + generations); // TODO: properties

    timeAreaBox.getChildren().addAll(speedSlider, dynamicGenerations);
    return timeAreaBox;
  }

  private HBox createButtonBoxArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause,
      Runnable play, Runnable reset, Runnable createNewGameAction, Runnable clear,
      Runnable stepForward,
      Runnable stepBack) {
    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    // TODO: remove dependence on names twice
    buttons.addActionToButton("Clear", e -> clear.run());
    buttons.addActionToButton("Reset", e -> reset.run());
    buttons.addActionToButton("Save", e -> onSave.run());
    buttons.addActionToButton("Load", e -> onLoad.run());
    buttons.addActionToButton("Forward", e -> stepForward.run());
    buttons.addActionToButton("Back", e -> stepBack.run());
    buttons.addActionToButton("Play", e -> playButtonFunction(play, pause));
    buttons.addActionToButton("NewGame", e -> createNewGameAction.run());

    HBox buttonBoxArea = new HBox();
    buttonBoxArea.setId("button-box-area");

    for (String name : names) {
      buttonBoxArea.getChildren().add(buttons.getButton(name));
    }

    return buttonBoxArea;
  }

  private VBox createToggleArea(String[] names, Runnable toggleOutline) {
    VBox toggle = new VBox(10);

    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    buttons.addActionToButton("Outline", event -> {
      toggleOutline.run();
    });

    for (String name : names) {
      toggle.getChildren().add(buttons.getButton(name));
    }
    toggle.setId("toggle-area-box");
    return toggle;
  }

  private void playButtonFunction(Runnable play, Runnable pause) {
    if (onPlay) {
      simPausedButtonChange();
      pause.run();
    } else {
      onPlay = true;
      buttons.setButtonText("Play", "Pause");
      play.run();
    }
  }

  public void simPausedButtonChange() { // FIXME: better method name; javadoc
    onPlay = false;
    buttons.setButtonText("Play", "Play");
  }

  public double getSliderValue() {
    if (speedSlider.getValue() <= 0) {
      return 1;
    }
    return speedSlider.getValue();
  }

  public void setGenerations(int generations) {
    this.generations = generations;
    dynamicGenerations.setText(
        "Gen: " + this.generations);
  }

  public ParseResult getResultChanges() {
    ParseResult changedResult = infoView.getEditResults();
    parseHolder = changedResult;
    return changedResult;
  }

  public Optional<File> startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    File initialDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data")
            .toString()); // FIXME: make restriction work, probably due to absolute path
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    if (initialDir.exists()
        && initialDir.isDirectory()) { // FIXME: maybe doesn't work because initial dir doesn't exist, see fixme above
      fileChooser.setInitialDirectory(initialDir);
    } else {
      File userHome = new File(
          System.getProperty("user.home")); // FIXME: this is being called on my system
      fileChooser.setInitialDirectory(userHome);
    }
    File chosen = fileChooser.showOpenDialog(stage);
    return Optional.ofNullable(chosen);
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/GameScreen/GameScreen", locale);
  }
}