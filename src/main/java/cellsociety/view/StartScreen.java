package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The application launch screen shown before a simulation is loaded.
 *
 * <p>Presents buttons to choose a language, select a visual theme, and open a
 * simulation file. Delegates locale changes to {@link LanguageView} and
 * exposes the selected locale via {@link #getLocale()}.</p>
 *
 * @author cellsociety team
 */

public class StartScreen extends StylableView {
  private static final Logger LOG = LogManager.getLogger(StartScreen.class);

  private LanguageView languageView;
  private ThemeView themeView;
  private ButtonCreator buttonCreator;
  private ResourceBundle screenText;
  // TODO: figure out default_resource_folder across multiple files

  public StartScreen(Stage stage, Runnable start) {
    languageView = new LanguageView();
    buttonCreator = new ButtonCreator();
    themeView = new ThemeView();


    String[] names = {"Language", "Theme", "Choice"}; // TODO: properties

    for (String name : names) {
      buttonCreator.addEmptyButton(name);
    }
    buttonCreator.addActionToButton("Language", e -> setLanguage());
    buttonCreator.addActionToButton("Theme", e -> setTheme());
    buttonCreator.addActionToButton("Choice", e -> start.run());

    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);
    registerThemeListenerWithLayoutSheet(scene);
    registerLanguageListener();

    stage.setOnHidden(event -> unregisterAllListeners());

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
      buttonHBox.getChildren().add(buttonCreator.getButton(name));
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
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/StartScreen/StartScreen", locale);
  }
}
