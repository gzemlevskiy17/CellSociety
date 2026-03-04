package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Presents a language-selection dialog and tracks the active {@link java.util.Locale}.
 *
 * <p>Available languages are loaded from the {@code view/Text/Languages} resource bundle.
 * The selected locale is propagated to other UI components via {@link #getLocale()}.</p>
 *
 * @author cellsociety team
 */

public class LanguageView extends StylableView {
  private static final Logger LOG = LogManager.getLogger(LanguageView.class);
  private List<String> languageChoices;
  private Map<String, Locale> languagetoLocale;
  private ResourceBundle screenText;

  public LanguageView() {
    super();
    registerLanguageListener();
    languagetoLocale = new HashMap<>();
    loadLanguages();
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/StartScreen/LanguageView/LanguageView", locale);
  }

  private void loadLanguages() {
    ResourceBundle languageBundle = null;
    try {
      languageBundle = PropertiesLoader.getResourceBundle(
          "view/Text/Languages");
    } catch (NullPointerException e) {
      String message = String.format("Failed to load language list, defaulting to %s.",
          languageManager.getDefaultLocale().getDisplayLanguage());
      LOG.warn(message, e);
      throw new IllegalStateException(message,
          e);
    }

    String languages = languageBundle.getString("languages");
    languageChoices = Arrays.asList(languages.split(","));
    for (String language : languageChoices) {
      String localeString = languageBundle.getString(language);
      languagetoLocale.put(language, Locale.of(localeString));
    }

    languageManager.setDefaultLocale(
        languagetoLocale.get(languageBundle.getString("defaultLanguage")));
    languageManager.setLocale(languageManager.getDefaultLocale());
  }

  public void languageChoiceScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(languageManager.getDefaultLanguage(),
        languageChoices);
    // dialog.setTitle("Language of Cell Society"); //TODO: need header?
    dialog.setHeaderText(screenText.getString("header"));
    dialog.setContentText(screenText.getString("prompt"));
    Optional<String> result = dialog.showAndWait();
    Scene scene = dialog.getDialogPane().getScene();

    registerThemeListener(scene);

    dialog.setOnHidden(event -> unregisterAllListeners());

    // Don't need default choice as it will be set in loadLanguages()
    result.ifPresent(choice -> languageManager.setLocale(languagetoLocale.get(choice)));
  }
}
