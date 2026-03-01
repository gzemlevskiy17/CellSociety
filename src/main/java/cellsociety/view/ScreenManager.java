package cellsociety.view;

import static cellsociety.controller.PropertiesLoader.DEFAULT_RESOURCE_FOLDER;

import cellsociety.controller.PropertiesLoader;
import java.io.File;
import java.nio.file.Paths;
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

public class ScreenManager {

  private Stage stageLive;
  private GridView gridView;
  private InfoView infoView;
  private ButtonCreator buttons;
  private String language;
  private boolean onPlay;
  private Label dynamicGenerations;
  private Slider speedSlider;
  private int generations;

  private ResourceBundle layoutResources;

  //TODO: remove hardcoding
  public static final String STYLESHEET = "light.css";
  private static final String LAYOUT_STYLESHEET = "GameScreen.css";

  public ScreenManager(Stage stage, String langResult) {
    stageLive = stage;
    language = langResult;
    try {
      layoutResources = PropertiesLoader.loadProperties("Config/" + language);
    } catch (Exception e) {
      e.printStackTrace(); //TODO: handle
    }
  }

  public void checkAllNotNull(Object... params) throws Exception { //TODO: do we need this
    for (Object param : params) {
      if (param == null) {
        throw new NullPointerException("A parameter is null.");
      }
    }
  }

  public void setGridView(GridView newGridView) {
    gridView = newGridView;
  }

  public void gameScreen(Runnable onSave, Runnable onLoad, Runnable pause, Runnable play,
      Runnable reset, Runnable setGameSpeed,
      Runnable createNewGameAction, Runnable clear, Runnable stepForward, Runnable stepBack,
      int startingGenerations,
      double startSpeed) {
    generations = startingGenerations;
    onPlay = true; //TODO: make more elegant
    buttons = new ButtonCreator(language);
    String[] names = { "Play", "Reset", "Forward", "Back", "Clear", "Save", "Load", "Edit", "NewGame" };
    String[] info = { "Info", "EditInfo" };

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    BorderPane titleBox = createTopBox(info);
    mainScreen.setTop(titleBox);

    VBox simulationControl = createControlArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction, clear, stepForward, stepBack, startSpeed);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.getGameName(); // TODO: language
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
  }

  private BorderPane createTopBox(String[] names) {
    Label topLabel = new Label(gridView.getGameName());
    topLabel.setFont(new Font("Comic Sans", 50)); // TODO: put in style
    topLabel.setAlignment(Pos.CENTER);
    BorderPane titleBox = new BorderPane(topLabel);

    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    // buttons.addActionToButton("Info", e -> );
    // buttons.addActionToButton("EditInfo", e -> );
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

    speedSlider = new Slider(0, 10, startSpeed); //TODO: properties
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(1); //TODO: properties
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

    //TODO: remove dependence on names twice
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

  public void simPausedButtonChange() {
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
            .toString()); //FIXME: make restriction work, probably due to absolute path
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    // fileChooser.setTitle("Open My File"); //TODO: need title?
    if (initialDir.exists()
        && initialDir.isDirectory()) { //FIXME: maybe doesn't work because initial dir doesn't exist, see fixme above
      fileChooser.setInitialDirectory(initialDir);
    } else {
      File userHome = new File(System.getProperty("user.home"));
      fileChooser.setInitialDirectory(userHome);
    }
    File chosen = fileChooser.showOpenDialog(stage);
    return Optional.ofNullable(chosen);
  }
}