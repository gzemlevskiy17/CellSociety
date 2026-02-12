package cellsociety.view;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
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

  public ScreenManager(Stage stage, GridView gridToUse) {
    stageLive = stage;
    gridView = gridToUse;
    gameScreen();
    stage.show();
  }

  public void gameScreen() {
    BorderPane mainScreen = new BorderPane(gridView.getGridPane());

    Label topLabel = new Label(gridView.gameName);
    topLabel.setFont(new Font("Comic Sans", 50));
    topLabel.setAlignment(Pos.CENTER);
    VBox titleBox = new VBox(topLabel);
    titleBox.setAlignment(Pos.TOP_CENTER);
    mainScreen.setTop(titleBox);

    Button resetButton = new Button("Reset");
    Button saveButton = new Button("Save/Edit");
    Button clearButton = new Button("Clear");
    Button loadButton = new Button("Load");
    Button startstopButton = new Button("Start");

    Button addSpeedButton = new Button("+");
    Button minusSpeedButton = new Button("--");
    Slider speedSlider = new Slider(0, 60, 1);
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(7);
    speedSlider.setSnapToTicks(true);

    VBox simulationControl = new VBox(15);
    HBox hbox = new HBox(30);
    hbox.setAlignment(Pos.BOTTOM_CENTER);
    hbox.setPadding(new Insets(0, 0, 30, 0));
    hbox.getChildren().addAll(resetButton, saveButton, clearButton,
        loadButton, startstopButton, addSpeedButton, minusSpeedButton);
    simulationControl.getChildren().addAll(speedSlider, hbox);
    simulationControl.setAlignment(Pos.CENTER);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.gameName;
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
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

  private boolean checkFileNormal(File file) {
    if (Objects.isNull(file) || file.isFile()) {
      return false;
    }
    return true;
  }

  public File startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    String relativePath = "resources";
    File initialDirectory = new File(Paths.get(".").toAbsolutePath().normalize().resolve(relativePath).toString());
    ExtensionFilter onlyXML = new ExtensionFilter("only XML", "*.xml");

    fileChooser.getExtensionFilters().add(onlyXML);
    fileChooser.setTitle("Open My File");
    fileChooser.setInitialDirectory(initialDirectory);

    File selectedFile = null;
    boolean fileThere = false;
    while (!fileThere) {
      selectedFile = fileChooser.showOpenDialog(stage);
      if (checkFileNormal(selectedFile)) {
        fileThere = true;
      }
    }
    return selectedFile;
  }

  public void editSaveScreen() {

  }

  public void endScreen() {

  }
}
