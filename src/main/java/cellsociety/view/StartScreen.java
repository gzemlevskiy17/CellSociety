package cellsociety.view;

import static cellsociety.controller.PropertiesLoader.DEFAULT_RESOURCE_FOLDER;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The application launch screen shown before a simulation is loaded.
 *
 * <p>Presents buttons to choose a language, select a visual theme, and open a
 * simulation file. Delegates locale changes to {@link LanguageView} and
 * exposes the selected locale via {@link #getLocale()}.</p>
 *
 * @author cellsociety team
 */
public class StartScreen {

    public static final String THEME_STYLESHEET = "light.css";
    private LanguageView languageView;
    private static final String DEFAULT_THEME = "Light"; // TODO: set defaults
    private ButtonCreator firstButton;
    private String[] themeChoices = { "Light", "Dark" }; // TOOD: load dynamically
    private static final Logger LOG = LogManager.getLogger(StartScreen.class);
    private static final String LAYOUT_STYLESHEET = "StartScreen.css";
    private static final String CONFIG_RESOURCE_FOLDER = "view/Config/";
    private static final String GAME_SCREEN_CONFIG = "StartScreen"; // TODO: parameterize
    // TODO: figure out default_resource_folder across multiple files

    public StartScreen(Stage stage, Runnable start) {
        languageView = new LanguageView();
        firstButton = new ButtonCreator(languageView.getLocale());

        String[] names = { "Language", "Theme", "Choice" }; // TODO: properties

        for (String name : names) {
            firstButton.addEmptyButton(name);
        }
        firstButton.addActionToButton("Language", e -> setLanguage());
        firstButton.addActionToButton("Theme", e -> findTheme());
        firstButton.addActionToButton("Choice", e -> start.run());

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        addStyleSheets(scene);

        HBox hBox = new HBox(); // TODO: rename to be more descriptive
        hBox.setId("hbox");

        Label bigLabel = new Label("Cell Society");
        bigLabel.setId("big-label");
        bigLabel.setMaxWidth(Double.MAX_VALUE); // TODO: Properties
        bigLabel.setPrefHeight(100);
        hBox.getChildren().add(bigLabel);

        HBox buttonHBox = new HBox();
        buttonHBox.setId("button-hbox");

        for (String name : names) {
            buttonHBox.getChildren().add(firstButton.getButton(name));
        }
        root.setTop(hBox);
        root.setCenter(buttonHBox);

        stage.setTitle("Launcher"); // TODO: properties
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void addStyleSheets(
            Scene scene) { // TODO: deduplicate (create new class that handles variables and maybe
                           // stylesheet creation)?
        String importedThemeStyleSheet = null;
        String themeStyleSheetPath = DEFAULT_RESOURCE_FOLDER + "Style/" + THEME_STYLESHEET;
        try {
            importedThemeStyleSheet = getClass().getResource(
                    themeStyleSheetPath)
                    .toExternalForm();
        } catch (NullPointerException e) {
            String message = String.format("Failed to load theme stylesheet from %s.",
                    themeStyleSheetPath);
            LOG.warn(message, e);
            throw new RuntimeException(message, e);
        }

        String importedLayoutStyleSheet = null;
        String layoutStyleSheetPath = DEFAULT_RESOURCE_FOLDER + "Style/" + LAYOUT_STYLESHEET;
        try {
            importedLayoutStyleSheet = getClass().getResource(
                    layoutStyleSheetPath)
                    .toExternalForm();
        } catch (NullPointerException e) {
            String message = String.format("Failed to load layout stylesheet from %s.",
                    layoutStyleSheetPath);
            LOG.warn(message, e);
            throw new RuntimeException(message, e);
        }

        scene.getStylesheets()
                .addAll(
                        importedThemeStyleSheet,
                        importedLayoutStyleSheet);
    }

    private void findTheme() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_THEME, Arrays.asList(themeChoices));
        dialog.setTitle("Theming Cell Society"); // TODO: properties
        dialog.setHeaderText("Theme Setting");
        dialog.setContentText("Choose your theme:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(choice -> setTheme(choice), () -> setTheme(DEFAULT_THEME));
    }

    private void setTheme(String theme) {
        String chosenTheme = theme;
    }

    private void setLanguage() {
        languageView.languageScreen();
        firstButton.changeLocale(getLocale());
    }

    public Locale getLocale() {
        return languageView.getLocale();
    }
}
