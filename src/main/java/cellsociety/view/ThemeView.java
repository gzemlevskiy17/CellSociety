package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * UI component that presents a dialog for users to select a visual theme.
 *
 * <p><b>Purpose:</b> Allows users to change the application's appearance by selecting from a list
 * of loaded themes.</p>
 *
 * <p><b>Usage:</b> Call {@link #themeChoiceScreen()} to display the selection dialog. The choice is
 * propagated to the
 * {@link ThemeManager}, which updates all subscribing views.</p>
 *
 * <p><b>Justification:</b> Extends {@link StylableView} to ensure the dialog itself respects the
 * current language settings.
 * It acts as the "Write" interface to the {@link ThemeManager}, allowing user input to modify the
 * global theme state.</p>
 *
 * @author Georgiy Zemlevskiy
 */
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
  }

  public void themeChoiceScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(themeManager.getDefaultTheme(), themeChoices);
    // dialog.setTitle("Theme of Cell Society"); //TODO: need header?
    dialog.setHeaderText(screenText.getString("header"));
    dialog.setContentText(screenText.getString("prompt"));
    Scene scene = dialog.getDialogPane().getScene();

    registerThemeListener(scene);
    dialog.setOnHidden(event -> unregisterAllListeners());
    Optional<String> result = dialog.showAndWait();

    result.ifPresent(choice -> themeManager.setTheme(choice));
  }
}
