package cellsociety.view;

import java.util.Locale;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartScreen {

  private static final Logger LOG = LogManager.getLogger(StartScreen.class);

  private LanguageView languageView;
  private ThemeView themeView;
  private ButtonCreator firstButton;
  private StyleLoader styleLoader;
  // TODO: figure out default_resource_folder across multiple files

  public StartScreen(Stage stage, Runnable start) {
    languageView = new LanguageView();
    firstButton = new ButtonCreator(languageView.getLocale());
    themeView = new ThemeView(languageView.getLocale());
    styleLoader = new StyleLoader();

    String[] names = {"Language", "Theme", "Choice"}; // TODO: properties

    for (String name : names) {
      firstButton.addEmptyButton(name);
    }
    firstButton.addActionToButton("Language", e -> setLanguage());
    firstButton.addActionToButton("Theme", e -> setTheme());
    firstButton.addActionToButton("Choice", e -> start.run());

    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);
    styleLoader.addStyleSheets(scene, "Light", "StartScreen");

    HBox hBox = new HBox(); // TODO: rename to be more descriptive
    hBox.setId("hbox");

    Label bigLabel = new Label("Cell Society");
    bigLabel.setId("big-label");
    bigLabel.setMaxWidth(Double.MAX_VALUE); // TODO: Properties
    bigLabel.setPrefHeight(100);
    hBox.getChildren().add(bigLabel);

    HBox buttonHBox = new HBox();
    buttonHBox.setId("button-hbox");

    for (String name : names) {
      buttonHBox.getChildren().add(firstButton.getButton(name));
    }
    root.setTop(hBox);
    root.setCenter(buttonHBox);

    stage.setTitle("Launcher"); // TODO: properties
    stage.setScene(scene);
    stage.sizeToScene();
    stage.show();
  }

  private void setTheme() {
    themeView.themeChoiceScreen();
  }

  private void setLanguage() {
    languageView.languageChoiceScreen();
    firstButton.changeLocale(getLocale());
  }

  public Locale getLocale() {
    return languageView.getLocale();
  }
}
