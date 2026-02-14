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

  public ScreenManager(Stage stage, GridView gridToUse) {
    stageLive = stage;
    gridView = gridToUse;
    buttons = new ButtonCreator();
    languager = new LanguageView();
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

    String[] names = { "Play", "Reset", "Clear", "Save", "Load", "Edit" };
    for (int i = 0; i < names.length; i++) {
      buttons.addEmptyButton(names[i]);
    }

    Slider speedSlider = new Slider(0, 60, 1);
    speedSlider.setPrefWidth(400);
    speedSlider.setMaxWidth(400);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(6);
    speedSlider.setSnapToTicks(true);

    buttons.addActionToButton("Reset", e -> {
      if (reset != null)
        reset.run();
    });
    buttons.addActionToButton("Save", e -> {
      if (onSave != null)
        onSave.run();
    });
    buttons.addActionToButton("Load", e -> {
      if (onLoad != null)
        onLoad.run();
    });
    buttons.addActionToButton("Play", e -> {
      if (play != null && pause != null) {
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
    });

    speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      pause.run();
      onPlay = true;
      buttons.getButton("Play").setText("Play");
      sliderValue = newValue.doubleValue();
      setGameSpeed.run();
    });

    HBox hbox = new HBox(30);
    hbox.setAlignment(Pos.BOTTOM_CENTER);
    hbox.setPadding(new Insets(0, 0, 30, 0));

    Iterator<Button> currentButtons = buttons.returnAll();
    while (currentButtons.hasNext()) {
      hbox.getChildren().add(currentButtons.next());
    }

    VBox simulationControl = new VBox(15);
    simulationControl.getChildren().addAll(speedSlider, hbox);
    simulationControl.setAlignment(Pos.CENTER);
    mainScreen.setBottom(simulationControl);

    Scene scene = new Scene(mainScreen, 1000, 800);
    String titleOfWindow = "Cell Society: Running " + gridView.gameName;
    stageLive.setTitle(titleOfWindow);
    stageLive.setScene(scene);
  }

  public void addButtons(String[] names) {

  }

  public double getSliderValue() {
    if (sliderValue <= 0)
      return 1;
    return sliderValue;
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

  public String getLanguage() {
    return languager.getLang();
  }
}