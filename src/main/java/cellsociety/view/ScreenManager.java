package cellsociety.view;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
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
  private static final String[] langChoices = { "English" };
  private static final String DEFAULT_LANGUAGE = "English";
  private String choosenLang = "";
  private double sliderValue = 0.0;
  private boolean onPlay;

  public ScreenManager(Stage stage, GridView gridToUse) {
    stageLive = stage;
    gridView = gridToUse;
  }

  public void setGridView(GridView newGridView) {
    gridView = newGridView;
  }

  public void gameScreen(Runnable onSave, Runnable onLoad, Runnable pause, Runnable play,
      Runnable reset, Runnable setGameSpeed) {
    onPlay = true;

    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    Label topLabel = new Label(gridView.gameName);
    topLabel.setFont(new Font("Comic Sans", 50));
    topLabel.setAlignment(Pos.CENTER);
    VBox titleBox = new VBox(topLabel);
    titleBox.setAlignment(Pos.TOP_CENTER);
    mainScreen.setTop(titleBox);

    Button resetButton = new Button("Reset");
    Button saveButton = new Button("Save");
    Button loadButton = new Button("Load");
    Button editButton = new Button("Edit");
    Button clearButton = new Button("Clear");
    Button playpauseButton = new Button("Play");

    Button addSpeedButton = new Button("+");
    Button minusSpeedButton = new Button("--");
    Slider speedSlider = new Slider(0, 60, 1);
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(6);
    speedSlider.setSnapToTicks(true);
    resetButton.setOnAction(e -> {
      if (reset != null)
        reset.run();
    });
    saveButton.setOnAction(e -> {
      if (onSave != null)
        onSave.run();
    });
    loadButton.setOnAction(e -> {
      if (onLoad != null)
        onLoad.run();
    });
    playpauseButton.setOnAction(e -> {
      if (play != null && pause != null) {
        if (onPlay) {
          onPlay = false;
          playpauseButton.setText("Pause");
          play.run();
        } else {
          onPlay = true;
          playpauseButton.setText("Play");
          pause.run();
        }
      }
    });

    speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      pause.run();
      onPlay = true;
      playpauseButton.setText("Play");
      sliderValue = newValue.doubleValue();
      setGameSpeed.run();
    });

    VBox simulationControl = new VBox(15);
    HBox hbox = new HBox(30);
    hbox.setAlignment(Pos.BOTTOM_CENTER);
    hbox.setPadding(new Insets(0, 0, 30, 0));
    hbox.getChildren().addAll(resetButton, saveButton, clearButton,
        loadButton, playpauseButton, addSpeedButton, minusSpeedButton);
    simulationControl.getChildren().addAll(speedSlider, hbox);
    simulationControl.setAlignment(Pos.CENTER);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.gameName;
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
  }

  public double getSliderValue() {
    if (sliderValue <= 0)
      return 1;
    return sliderValue;
  }

  public String languageScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, Arrays.asList(langChoices));
    dialog.setTitle("Initializing Cell Society");
    dialog.setHeaderText("Language Setting");
    dialog.setContentText("Choose your language:");
    Optional<String> result = dialog.showAndWait();
    result.ifPresentOrElse(choice -> setLang(choice), () -> setLang(DEFAULT_LANGUAGE));
    return choosenLang;
  }

  private void setLang(String language) {
    choosenLang = language;
  }

  public File startScreen(Stage stage) {
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
    return fileChooser.showOpenDialog(stage);
  }
}
