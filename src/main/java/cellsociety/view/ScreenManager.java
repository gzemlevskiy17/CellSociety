package cellsociety.view;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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
  private LanguageView languager;
  private double sliderValue = 0.0;
  private boolean onPlay;

  public ScreenManager(Stage stage) {
    stageLive = stage;
    languager = new LanguageView();
  }

  public void checkAllNotNull(Object... params) {
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
      Runnable reset, Runnable setGameSpeed, Runnable createNewGameAction) { // TODO: implement createNewGame button
    checkAllNotNull(onSave, onLoad, pause, play, reset, setGameSpeed, createNewGameAction);

    onPlay = true;
    buttons = new ButtonCreator();

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    Label topLabel = new Label(gridView.gameName);
    topLabel.setFont(new Font("Comic Sans", 50));
    topLabel.setAlignment(Pos.CENTER);
    VBox titleBox = new VBox(topLabel);
    titleBox.setAlignment(Pos.TOP_CENTER);
    mainScreen.setTop(titleBox);

    String[] names = { "Play", "Reset", "Clear", "Save", "Load", "Edit", "NewGame" };

    VBox simulationControl = createControlArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.gameName;
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
  }

  private VBox createControlArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause, Runnable play,
      Runnable reset, Runnable setGameSpeed, Runnable createNewGameAction) {

    Slider speedSlider = new Slider(0, 60, 1);
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(6);
    speedSlider.setSnapToTicks(true);

    speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      speedSliderFunction(pause, setGameSpeed, newValue.doubleValue());
    });

    HBox hbox = createButtonBoxArea(names, onSave, onLoad, pause, play,
        reset, setGameSpeed, createNewGameAction);

    VBox simulationControl = new VBox(15);
    simulationControl.getChildren().addAll(speedSlider, hbox);
    simulationControl.setAlignment(Pos.CENTER);
    return simulationControl;
  }

  private HBox createButtonBoxArea(String[] names, Runnable onSave, Runnable onLoad, Runnable pause, Runnable play,
      Runnable reset, Runnable setGameSpeed, Runnable createNewGameAction) {
    for (int i = 0; i < names.length; i++) {
      buttons.addEmptyButton(names[i]);
    }

    buttons.addActionToButton("Reset", e -> {
      reset.run();
    });
    buttons.addActionToButton("Save", e -> {
      onSave.run();
    });
    buttons.addActionToButton("Load", e -> {
      onLoad.run();
    });
    buttons.addActionToButton("Play", e -> {
      playButtonFunction(play, pause);
    });
    buttons.addActionToButton("NewGame", e -> {
      createNewGameAction.run();
    });

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
      buttons.getButton("Play").setText("Pause");
      play.run();
    } else {
      onPlay = true;
      buttons.getButton("Play").setText("Play");
      pause.run();
    }
  }

  private void speedSliderFunction(Runnable pause, Runnable setGameSpeed, double newValue) {
    pause.run();
    onPlay = true;
    buttons.getButton("Play").setText("Play");
    sliderValue = newValue;
    setGameSpeed.run();
  }

  public double getSliderValue() {
    if (sliderValue <= 0) {
      return 1;
    }
    return sliderValue;
  }

  public Optional<File> startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    File initialDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data").toString());
    ExtensionFilter onlyXML = new ExtensionFilter("*.xml", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    fileChooser.setTitle("Open My File");
    if (initialDir.exists() && initialDir.isDirectory()) {
      fileChooser.setInitialDirectory(initialDir);
    } else {
      File userHome = new File(System.getProperty("user.home"));
      fileChooser.setInitialDirectory(userHome);
    }
    File chosen = fileChooser.showOpenDialog(stage);
    Optional<File> returnObject = Optional.ofNullable(chosen);
    return returnObject;
  }

  public String getLanguage() {
    return languager.getLang();
  }
}