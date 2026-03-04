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
 * simulation file. Delegates locale changes to {@link LanguageView} *
 *
 * @author cellsociety team
 */

public class StartScreen extends StylableView {

  private static final Logger LOG = LogManager.getLogger(StartScreen.class);

  private ButtonCreator buttonCreator;
  private ThemeView themeView;
  private LanguageView languageView;
  private ResourceBundle screenText;
  private ResourceBundle config;

  public StartScreen(Stage stage, Runnable start) {
    themeView = new ThemeView();
    languageView = new LanguageView();
    buttonCreator = new ButtonCreator();
    config = PropertiesLoader.getResourceBundle("view/Config/StartScreen/StartScreen");
    registerLanguageListener();

    String[] names = config.getString("buttonNames").split("[,\s]+");

    for (String name : names) {
      buttonCreator.addEmptyButton(name);
    }
    buttonCreator.addActionToButton("Language", e -> setLanguage()); //TODO: remove duplicate names
    buttonCreator.addActionToButton("Theme", e -> setTheme());
    buttonCreator.addActionToButton("Choice", e -> start.run());

    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);
    registerThemeListenerWithLayoutSheet(scene);

    stage.setOnHidden(event -> unregisterAllListeners());

    HBox hBox = new HBox(); // TODO: rename to be more descriptive
    hBox.setId("hbox");

    Label bigLabel = new Label("Cell Society");
    bigLabel.setId("big-label");
    bigLabel.setMaxWidth(Double.parseDouble(config.getString("titleLabel.maxWidth")));
    bigLabel.setPrefHeight(Double.parseDouble(config.getString("titleLabel.prefHeight")));
    hBox.getChildren().add(bigLabel);

    HBox buttonHBox = new HBox();
    buttonHBox.setId("button-hbox");

    for (String name : names) {
      buttonHBox.getChildren().add(buttonCreator.getButton(name));
    }
    root.setTop(hBox);
    root.setCenter(buttonHBox);

    stage.setTitle(screenText.getString("window.title"));
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
