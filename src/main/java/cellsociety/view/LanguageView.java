package cellsociety.view;

import java.util.Arrays;
import java.util.Optional;

import javafx.scene.control.ChoiceDialog;

public class LanguageView {

    private static final String[] langChoices = { "English" };
    private static final String DEFAULT_LANGUAGE = "English";
    private String choosenLang = "";

    public LanguageView() {
        languageScreen();
    }

    private void languageScreen() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_LANGUAGE, Arrays.asList(langChoices));
        dialog.setTitle("Initializing Cell Society");
        dialog.setHeaderText("Language Setting");
        dialog.setContentText("Choose your language:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(choice -> setLang(choice), () -> setLang(DEFAULT_LANGUAGE));
    }

    private void setLang(String language) {
        choosenLang = language;
    }

    public String getLang() {
        return choosenLang;
    }
}
