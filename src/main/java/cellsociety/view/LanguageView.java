package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageView {

  private static final Logger LOG = LogManager.getLogger(LanguageView.class);
  private static Locale DEFAULT_LOCALE = Locale.getDefault();
  private static String DEFAULT_LANGUAGE = "English";
  private List<String> languageChoices;
  private Map<String, String> languageToLocaleString;
  private Locale locale;
  private ResourceBundle screenText;

  public LanguageView() {
    languageToLocaleString = new HashMap<>();
    loadLanguages();
  }

  private void loadLanguages() {
    ResourceBundle languageBundle = null;
    try {
      languageBundle = PropertiesLoader.getResourceBundle(
          "Text/Languages");
    } catch (NullPointerException e) {
      String message = String.format("Failed to load language list, defaulting to %s.",
          DEFAULT_LOCALE.getDisplayLanguage());
      locale = DEFAULT_LOCALE;
      LOG.warn(message, e);
      throw new IllegalStateException(message,
          e);
    }
    DEFAULT_LANGUAGE = languageBundle.getString("defaultLanguage");
    locale = DEFAULT_LOCALE;
    screenText = updateScreenTextBundle();

    String languages = languageBundle.getString("languages");
    languageChoices = Arrays.asList(languages.split(","));
    for (String language : languageChoices) {
      String localeString = languageBundle.getString(language);
      languageToLocaleString.put(language, localeString);
    }
  }

  private ResourceBundle updateScreenTextBundle() {
    return PropertiesLoader.getResourceBundleWithLocale(
        "Text/Screen/StartScreen/LanguageView/LanguageView", locale);
  }

  public void languageScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, languageChoices);
//    dialog.setTitle("Language of Cell Society"); //TODO: need header?
    dialog.setHeaderText(screenText.getString("header"));
    dialog.setContentText(screenText.getString("prompt"));
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(choice -> {
          locale = Locale.of(languageToLocaleString.get(choice));
          screenText = updateScreenTextBundle();
        }
    );
  }

  public Locale getLocale() {
    return locale;
  }
}
