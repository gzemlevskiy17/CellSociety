package cellsociety.view;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
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
    Scene scene = new Scene(gridView.getGridPane(), 400, 400);
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

  public void startScreen(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    String relativePath = "resources";
    File initialDirectory = new File(Paths.get(".").toAbsolutePath().normalize().resolve(relativePath).toString());

    fileChooser.setTitle("Open My File");
    fileChooser.setInitialDirectory(initialDirectory);

    File selectedFile = fileChooser.showOpenDialog(stage);

  }

  public void editSaveScreen() {

  }

  public void endScreen() {

  }
}
