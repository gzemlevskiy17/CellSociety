package cellsociety.view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import cellsociety.controller.PropertiesLoader;
import javafx.scene.control.ChoiceDialog;

public class LanguageView {

    private static final String DEFAULT_LANGUAGE = "English";
    private List<String> langChoices;
    private String chosenLanguage;

    public LanguageView() {
        findingLanguages();
        languageScreen();
    }

    private void findingLanguages() {
        ResourceBundle myResources = null;
        try {
            myResources = PropertiesLoader.loadProperties("Languages/CellSocietyLanguages");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String errorCodesStr = myResources.getString("languages");
        String[] errorCodesArray = errorCodesStr.split(",");
        langChoices = Arrays.asList(errorCodesArray);
    }

    private void languageScreen() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, langChoices);
        dialog.setTitle("Initializing Cell Society");
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
