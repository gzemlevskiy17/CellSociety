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

  public LanguageView() {
    languageToLocaleString = new HashMap<>();
    loadLanguages();
  }

  public void setup() { //TODO: comment
//    loadLanguages();
    languageScreen();
  }

  private void loadLanguages() {
    ResourceBundle myResources = null;
    try {
      myResources = PropertiesLoader.getResourceBundle(
          "Text/Languages"); //TODO: handle errors in this?
    } catch (NullPointerException e) {
      String message = String.format("Failed to load language list, defaulting to %s.",
          DEFAULT_LOCALE.getDisplayLanguage());
      locale = DEFAULT_LOCALE;
      LOG.warn(message, e);
      throw new IllegalStateException(message,
          e);
    }
    DEFAULT_LANGUAGE = myResources.getString("defaultLanguage");
    locale = DEFAULT_LOCALE;
    String languages = myResources.getString("languages");
    languageChoices = Arrays.asList(languages.split(","));
    for (String language : languageChoices) {
      String localeString = myResources.getString(language);
      languageToLocaleString.put(language, localeString);
    }
  }

  private void languageScreen() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, languageChoices);
    dialog.setTitle("Language of Cell Society"); //TODO: dynamic update
    dialog.setHeaderText("Language Setting");
    dialog.setContentText("Choose your language:");
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(choice -> locale = Locale.of(languageToLocaleString.get(choice)));
  }

  public Locale getLocale() {
    return locale;
  }
}
