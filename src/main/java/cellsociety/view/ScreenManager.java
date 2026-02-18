package cellsociety.view;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ScreenManager {

  private Stage stageLive;
  private GridView gridView;
  private ButtonCreator buttons;
  private LanguageView languageView;
  private boolean onPlay;
  private Label dynamicGenerations;
  private Slider speedSlider;
  private int gen;

  public ScreenManager(Stage stage) {
    stageLive = stage;
    languageView = new LanguageView();
  }

  public void checkAllNotNull(Object... params) throws Exception {
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
      Runnable createNewGameAction, Runnable clear, int genNum, double startSpeed) {
    try {
      checkAllNotNull(onSave, onLoad, pause, play, reset, setGameSpeed, createNewGameAction, clear);
    } catch (Exception e) {
      e.printStackTrace();
    }
    gen = genNum;
    onPlay = true;
    buttons = new ButtonCreator(languageView.getLanguage());

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    Label topLabel = new Label(gridView.gameName);
    topLabel.setFont(new Font("Comic Sans", 50)); // TODO: put in style
    topLabel.setAlignment(Pos.CENTER);
    VBox titleBox = new VBox(topLabel);
    titleBox.setAlignment(Pos.TOP_CENTER);
    mainScreen.setTop(titleBox);

    String[] names = { "Play", "Reset", "Clear", "Save", "Load", "Edit", "NewGame" };

    VBox simulationControl = createControlArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction, clear, startSpeed);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.gameName;
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
  }

  private VBox createControlArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause,
      Runnable play, Runnable reset, Runnable setGameSpeed, Runnable createNewGameAction, Runnable clear,
      double startSpeed) {

    HBox timeArea = createTimeAreaBox(pause, setGameSpeed, startSpeed);

    HBox buttonArea = createButtonBoxArea(names, onSave, onLoad, pause, play,
        reset, createNewGameAction, clear);

    VBox simulationControl = new VBox(15);
    simulationControl.getChildren().addAll(timeArea, buttonArea);
    simulationControl.setAlignment(Pos.CENTER);
    return simulationControl;
  }

  private HBox createTimeAreaBox(Runnable pause, Runnable setGameSpeed, double startSpeed) {
    HBox hbox = new HBox(20);

    speedSlider = new Slider(0, 10, startSpeed);
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(1);
    speedSlider.setSnapToTicks(true);

    speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      speedSliderFunction(pause, setGameSpeed, newValue.doubleValue());
    });

    dynamicGenerations = new Label();
    dynamicGenerations.setText("Gen: " + String.valueOf(gen));

    hbox.setAlignment(Pos.TOP_CENTER);
    hbox.getChildren().addAll(speedSlider, dynamicGenerations);
    return hbox;
  }

  private HBox createButtonBoxArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause,
      Runnable play,
      Runnable reset, Runnable createNewGameAction, Runnable clear) {
    for (String name : names) {
      buttons.addEmptyButton(name);
    }

    buttons.addActionToButton("Clear", e -> clear.run());
    buttons.addActionToButton("Reset", e -> reset.run());
    buttons.addActionToButton("Save", e -> onSave.run());
    buttons.addActionToButton("Load", e -> onLoad.run());
    buttons.addActionToButton("Play", e -> playButtonFunction(play, pause));
    buttons.addActionToButton("NewGame", e -> createNewGameAction.run());

    HBox hbox = new HBox(30);
    hbox.setAlignment(Pos.BOTTOM_CENTER);
    hbox.setPadding(new Insets(0, 0, 30, 0));

    Iterator<Button> currentButtons = buttons.returnAll();
    while (currentButtons.hasNext()) {
      hbox.getChildren().add(currentButtons.next());
    }

    return hbox;
  }

  private void playButtonFunction(Runnable play, Runnable pause) {
    if (onPlay) {
      onPlay = false;
      buttons.setButtonText("Play", "Pause");
      play.run();
    } else {
      onPlay = true;
      buttons.setButtonText("Play", "Play");
      pause.run();
    }
  }

  private void speedSliderFunction(Runnable pause, Runnable setGameSpeed, double newValue) {
    pause.run();
    onPlay = true;
    buttons.setButtonText("Play", "Play");
    setGameSpeed.run();
  } // TODO: refactor so that this is called from the controller on button press.

  public double getSliderValue() {
    if (speedSlider.getValue() <= 0) {
      return 1;
    }
    return speedSlider.getValue();
  }

  public void setGenerations(int generations) {
    gen = generations;
    dynamicGenerations.setText("#: " + String.valueOf(gen));
  }

  public Optional<File> startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    File initialDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data").toString());
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    // fileChooser.setTitle("Open My File"); //TODO: need title?
    if (initialDir.exists() && initialDir.isDirectory()) {
      fileChooser.setInitialDirectory(initialDir);
    } else {
      File userHome = new File(System.getProperty("user.home"));
      fileChooser.setInitialDirectory(userHome);
    }
    File chosen = fileChooser.showOpenDialog(stage);
    return Optional.ofNullable(chosen);
  }

  public String getLanguage() {
    return languageView.getLanguage();
  } // TODO: needed?

  public void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}