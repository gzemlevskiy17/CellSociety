package cellsociety.view;

import static cellsociety.controller.PropertiesLoader.DEFAULT_RESOURCE_FOLDER;

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

//TODO: comment
public class GameScreen {

  public static final String THEME_STYLESHEET = "light.css";
  private Stage stageLive;
  private GridView gridView;
  private InfoView infoView;
  private ButtonCreator buttons;
  private Locale locale;
  private boolean onPlay;
  private Label dynamicGenerations;
  private Slider speedSlider;
  private int generations;

  private ResourceBundle layoutResources;
  private static final Logger LOG = LogManager.getLogger(GameScreen.class);
  private static final String LAYOUT_STYLESHEET = "GameScreen.css";
  private static final String CONFIG_RESOURCE_FOLDER = "view/Config/";
  private static final String GAME_SCREEN_CONFIG = "GameScreen"; // TODO: parameterize
  // TODO: figure out default_resource_folder across multiple files

  public GameScreen(Stage stage, Locale locale) {
    stageLive = stage;
    this.locale = locale;
    layoutResources = PropertiesLoader.getResourceBundle(
        CONFIG_RESOURCE_FOLDER + GAME_SCREEN_CONFIG);
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
      Runnable createNewGameAction, Runnable clear, Runnable stepForward, Runnable stepBack,
      int startingGenerations,
      double startSpeed) {

    infoView = new InfoView(stageLive, parsingInfo, locale);
    generations = startingGenerations;
    onPlay = true; // TODO: make more elegant
    buttons = new ButtonCreator(locale);
    String[] names = { "Play", "Reset", "Forward", "Back", "Clear", "Save", "Load", "Edit",
        "NewGame" }; // TODO: reflection
    String[] info = { "Info", "EditInfo" };

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    BorderPane titleBox = createTopBox(info);
    mainScreen.setTop(titleBox);

    VBox simulationControl = createControlArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction, clear, stepForward, stepBack, startSpeed);
    mainScreen.setBottom(simulationControl);

    double windowWidth = Double.parseDouble(layoutResources.getString("window.width"));
    double windowHeight = Double.parseDouble(layoutResources.getString("window.height"));
    Scene scene = new Scene(mainScreen, windowWidth, windowHeight);
    addStyleSheets(scene);

    String titleOfWindow = "Cell Society: Running " + gridView.getGameName(); // TODO: language & properties

    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
    stageLive.centerOnScreen();
  }

  private void addStyleSheets(Scene scene) {
    String importedThemeStyleSheet = null;
    String themeStyleSheetPath = DEFAULT_RESOURCE_FOLDER + "Style/" + THEME_STYLESHEET;
    try {
      importedThemeStyleSheet = getClass().getResource(
          themeStyleSheetPath)
          .toExternalForm();
    } catch (NullPointerException e) {
      String message = String.format("Failed to load theme stylesheet from %s.",
          themeStyleSheetPath);
      LOG.warn(message, e);
      throw new RuntimeException(message, e);
    }

    String importedLayoutStyleSheet = null;
    String layoutStyleSheetPath = DEFAULT_RESOURCE_FOLDER + "Style/" + LAYOUT_STYLESHEET;
    try {
      importedLayoutStyleSheet = getClass().getResource(
          layoutStyleSheetPath)
          .toExternalForm();
    } catch (NullPointerException e) {
      String message = String.format("Failed to load layout stylesheet from %s.",
          layoutStyleSheetPath);
      LOG.warn(message, e);
      throw new RuntimeException(message, e);
    }

    scene.getStylesheets()
        .addAll(
            importedThemeStyleSheet,
            importedLayoutStyleSheet);
  }

  private BorderPane createTopBox(String[] names) {
    Label topLabel = new Label(gridView.getGameName());
    topLabel.setId("top-label");
    BorderPane titleBox = new BorderPane(topLabel);

    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    buttons.addActionToButton("Info", e -> infoView.showInfoView());
    buttons.addActionToButton("EditInfo", e -> infoView.showInfoEdit());
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

    double sliderMin = Double.parseDouble(layoutResources.getString("slider.min"));
    double sliderMax = Double.parseDouble(layoutResources.getString("slider.max"));
    speedSlider = new Slider(sliderMin, sliderMax, startSpeed);

    double sliderPrefWidth = Double.parseDouble(layoutResources.getString("slider.prefWidth"));
    double sliderMaxWidth = Double.parseDouble(layoutResources.getString("slider.maxWidth"));
    speedSlider.setPrefWidth(sliderPrefWidth);
    speedSlider.setMaxWidth(sliderMaxWidth);
    speedSlider.setShowTickMarks(true); // TODO: need properties?

    double majorTickUnit = Double.parseDouble(layoutResources.getString("slider.majorTickUnit"));
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

  public Optional<File> startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    File initialDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data")
            .toString()); // FIXME: make restriction work, probably due to absolute path
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    // fileChooser.setTitle("Open My File"); //TODO: need title?
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
}