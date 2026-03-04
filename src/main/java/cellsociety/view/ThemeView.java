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

public class ThemeView extends StylableView {

  private static final Logger LOG = LogManager.getLogger(ThemeView.class);
  private List<String> themeChoices;
  private ResourceBundle screenText;

  public ThemeView() {
    super();
    registerLanguageListener();
    loadThemes();
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/StartScreen/ThemeView/ThemeView", locale);
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

    Consumer<String> themeListener = registerThemeListener(scene);
    dialog.setOnHidden(event -> {
      themeManager.removeListener(themeListener);
    });

    result.ifPresentOrElse(choice -> themeManager.setTheme(choice),
        () -> themeManager.setTheme(themeManager.getDefaultTheme()));
  }
}
