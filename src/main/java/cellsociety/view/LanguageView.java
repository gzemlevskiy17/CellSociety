package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ChoiceDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageView {

    private static final String DEFAULT_LANGUAGE = "English"; // TODO: move to properties
    private static final Logger LOG = LogManager.getLogger(LanguageView.class);
    private List<String> langChoices;
    private String chosenLanguage = DEFAULT_LANGUAGE;

    public void setup() {
        findingLanguages();
        languageScreen();
    }

    private void findingLanguages() {
        ResourceBundle myResources = null;
        // change the way ResourceBundle loads to include Locale
        try {
            myResources = PropertiesLoader.loadProperties("Text/Languages/CellSocietyLanguages");
        } catch (Exception e) {
            String message = "Failed to load language list, defaulting to English";
            LOG.warn(message, e);
            throw new IllegalStateException(message,
                    e);
        }
        String errorCodes = myResources.getString("languages");
        String[] errorCodesArray = errorCodes.split(",");
        langChoices = Arrays.asList(errorCodesArray);
    }

    private void languageScreen() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, langChoices);
        dialog.setTitle("Language of Cell Society");
        dialog.setHeaderText("Language Setting");
        dialog.setContentText("Choose your language:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(choice -> setLanguage(choice), () -> setLanguage(DEFAULT_LANGUAGE));
    }

    private void setLanguage(String language) {
        chosenLanguage = language;
    }

    public String getLanguage() {
        return chosenLanguage;
    }
}
