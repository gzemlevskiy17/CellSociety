package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThemeView {

  private static final Logger LOG = LogManager.getLogger(ThemeView.class);
  private String DEFAULT_THEME = "light";
  private List<String> themeChoices;
  private String theme;
  private ResourceBundle screenText;
  private StyleLoader styleLoader;
  private ThemeManager themeManager;
  private Consumer<String> themeListener;

  public ThemeView(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/StartScreen/ThemeView/ThemeView", locale); //TODO: deal with string
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
          DEFAULT_THEME);
      theme = DEFAULT_THEME;
      LOG.warn(message, e);
      throw new IllegalStateException(message,
          e);
    }
    DEFAULT_THEME = themeBundle.getString("defaultTheme");
    theme = DEFAULT_THEME; //TODO: handle default theme stylesheet not being found
//    ThemeManager.setTheme(theme); //TODO: need to have global variable for this?

    String themes = themeBundle.getString("themes");
    themeChoices = Arrays.asList(themes.split(","));
    System.out.println(themeChoices);
  }

  public void themeChoiceScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_THEME, themeChoices);
    // dialog.setTitle("Language of Cell Society"); //TODO: need header?
    dialog.setHeaderText(screenText.getString("header"));
    dialog.setContentText(screenText.getString("prompt"));
    Optional<String> result = dialog.showAndWait();
    Scene scene = dialog.getDialogPane().getScene();
    styleLoader.addThemeStyleSheet(scene, theme);

    // Listen for theme changes while dialog is open
    themeListener = theme -> styleLoader.addThemeStyleSheet(scene, theme);
    themeManager.addListener(themeListener);

    dialog.setOnHidden(event -> {
      // Stop listening when closed
      themeManager.removeListener(themeListener);
    });

    result.ifPresent(choice -> {
      theme = choice;
      styleLoader.addThemeStyleSheet(scene, theme);
    });
  }

  public String getTheme() {
    return theme;
  }
}
