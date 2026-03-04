package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThemeView {

  private static final Logger LOG = LogManager.getLogger(ThemeView.class);
  private List<String> themeChoices;
  private ResourceBundle screenText;
  private StyleLoader styleLoader;
  private ThemeManager themeManager;
  private LanguageManager languageManager;

  public ThemeView() {
    languageManager = LanguageManager.getInstance();
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/StartScreen/ThemeView/ThemeView",
        languageManager.getLocale()); //TODO: deal with string
    themeManager = ThemeManager.getInstance();
    styleLoader = new StyleLoader();
    loadThemes();
  }

  private void loadThemes() {
    ResourceBundle themeBundle = null;
    try {
      themeBundle = PropertiesLoader.getResourceBundle(
          "view/Text/Themes"); //TODO: deal with string
    } catch (NullPointerException e) {
      String message = String.format("Failed to load theme list, defaulting to %s.",
          themeManager.getDefaultTheme());
      LOG.warn(message, e);
      throw new IllegalStateException(message,
          e);
    }
    themeManager.setDefaultTheme(themeBundle.getString(
        "defaultTheme")); //TODO: handle default theme not being found in stylesheet

    String themes = themeBundle.getString("themes");
    themeChoices = Arrays.asList(themes.split(","));
    System.out.println(themeChoices);
  }

  public void themeChoiceScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(themeManager.getDefaultTheme(), themeChoices);
    // dialog.setTitle("Theme of Cell Society"); //TODO: need header?
    dialog.setHeaderText(screenText.getString("header"));
    dialog.setContentText(screenText.getString("prompt"));
    Optional<String> result = dialog.showAndWait();
    Scene scene = dialog.getDialogPane().getScene();
    styleLoader.addThemeStyleSheet(scene, themeManager.getTheme());

    // Listen for theme changes while dialog is open
    Consumer<String> themeListener = theme -> styleLoader.addThemeStyleSheet(scene, theme);
    themeManager.addListener(themeListener);

    dialog.setOnHidden(event -> {
      // Stop listening when closed
      themeManager.removeListener(themeListener);
    });

    result.ifPresentOrElse(choice -> themeManager.setTheme(choice),
        () -> themeManager.setTheme(themeManager.getDefaultTheme()));
  }
}
