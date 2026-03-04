package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import cellsociety.model.ParseResult;
import cellsociety.view.grids.GridView;
import java.io.File;
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

  /**
   * Constructs a GameScreen bound to the given stage. The stage is not shown
   * until {@link #gameScreen} is called.
   *
   * @param stage the primary window for this simulation screen
   */
  public GameScreen(Stage stage) {
    myStage = stage;
    config = PropertiesLoader.getResourceBundle("view/Config/GameScreen/GameScreen");
    registerLanguageListener();
  }

  /**
   * Sets the grid view used by this screen. Must be called before
   * {@link #gameScreen} builds the layout.
   *
   * @param newGridView the rendered grid to display
   */
  public void setGridView(GridView newGridView) {
    gridView = newGridView;
  }

  /**
   * Builds and displays the simulation window with the grid, control buttons,
   * speed slider, and generation counter.
   *
   * @param parsingInfo         simulation metadata used to populate the info panel
   * @param onSave              called when the user clicks Save
   * @param onLoad              called when the user clicks Load
   * @param pause               called when the user pauses the simulation
   * @param play                called when the user resumes the simulation
   * @param reset               called when the user clicks Reset
   * @param setGameSpeed        called when the speed slider value changes
   * @param createNewGameAction called when the user opens a new game window
   * @param clear               called when the user clicks Clear
   * @param stepForward         called when the user steps forward one generation
   * @param stepBack            called when the user steps back one generation
   * @param pullChanges         called to apply any pending info-panel edits
   * @param toggleLine          called to toggle cell border visibility
   * @param startingGenerations the initial generation count to display
   * @param startSpeed          the initial speed slider value
   */
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
    String[] names = config.getString("buttonNames").split("[,\s]+"); // TODO: reflection
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

    String titleOfWindow = gridView.getGameName();

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
    dynamicGenerations.setId("generation-label");
    dynamicGenerations.setText(screenText.getString("generationPrefix") + " " + generations);

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
    toggle.setId("toggle-area-box");

    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    buttons.addActionToButton("Outline", event -> {
      toggleOutline.run();
    });

    for (String name : names) {
      toggle.getChildren().add(buttons.getButton(name));
    }
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

  /**
   * Updates the Play/Pause button to show "Play" and records that the
   * simulation is currently paused. Call this whenever the simulation stops
   * so the button label stays in sync with the actual state.
   */
  public void simPausedButtonChange() {
    onPlay = false;
    buttons.setButtonText("Play", "Play");
  }

  /**
   * Returns the current speed slider value, with a minimum of 1.0 so that
   * callers never receive a non-positive multiplier.
   *
   * @return the speed multiplier, always &ge; 1.0
   */
  public double getSliderValue() {
    if (speedSlider.getValue() <= 0) {
      return 1;
    }
    return speedSlider.getValue();
  }

  /**
   * Updates the generation counter label to show the given value.
   *
   * @param generations the current generation count to display
   */
  public void setGenerations(int generations) {
    this.generations = generations;
    dynamicGenerations.setText(screenText.getString("generationPrefix") + " " + this.generations);
  }

  /**
   * Reads any pending edits from the info panel and returns an updated
   * {@link ParseResult} reflecting those changes.
   *
   * @return a new {@link ParseResult} with any user-edited metadata or parameters applied
   */
  public ParseResult getResultChanges() {
    ParseResult changedResult = infoView.getEditResults();
    parseHolder = changedResult;
    return changedResult;
  }

  /**
   * Opens a file chooser dialog filtered to XML files and returns the chosen
   * file, or an empty {@link Optional} if the user cancelled.
   *
   * @param stage the owner window for the dialog
   * @return an {@link Optional} containing the chosen file, or empty if cancelled
   */
  public Optional<File> startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    File initialDir = new File("data/");
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    if (initialDir.exists()
        && initialDir.isDirectory()) {
      fileChooser.setInitialDirectory(initialDir);
    } else {
      File userHome = new File(
          System.getProperty("user.home"));
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