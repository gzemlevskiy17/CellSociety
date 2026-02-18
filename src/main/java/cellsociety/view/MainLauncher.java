package cellsociety.view;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.Iterator;

import cellsociety.view.ButtonCreator;
import cellsociety.view.LanguageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainLauncher {
    private LanguageView languageView;
    private ButtonCreator firstButt;

    private static final String DEFAULT_THEME = "Light";
    private String[] langChoices = { "Light", "Dark" };
    private String chosenTheme = DEFAULT_THEME;

    public MainLauncher(Stage stage, Runnable start) {
        languageView = new LanguageView();
        firstButt = new ButtonCreator(languageView.getLanguage());

        String[] names = { "Language", "Theme", "Choice" };

        for (String name : names) {
            firstButt.addEmptyButton(name);
        }
        firstButt.addActionToButton("Language", e -> languageView.setup());
        firstButt.addActionToButton("Theme", e -> findTheme());
        firstButt.addActionToButton("Choice", e -> start.run());

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        HBox hBox = new HBox();
        Label bigLabel = new Label("Cell Society");
        bigLabel.setMaxWidth(Double.MAX_VALUE);
        bigLabel.setPrefHeight(100);
        bigLabel.setFont(Font.font("Comic Sans", FontWeight.BOLD, 36));
        bigLabel.setAlignment(Pos.CENTER);
        bigLabel.setTextAlignment(TextAlignment.CENTER);
        hBox.getChildren().add(bigLabel);
        hBox.setAlignment(Pos.TOP_CENTER);
        HBox buttonHBox = new HBox(30);
        buttonHBox.setPadding(new Insets(0, 30, 30, 30));

        Iterator<Button> currentButtons = firstButt.returnAll();
        while (currentButtons.hasNext()) {
            buttonHBox.getChildren().add(currentButtons.next());
        }
        root.setTop(hBox);
        root.setCenter(buttonHBox);

        File cssFile = new File(Paths.get(".").toAbsolutePath().normalize()
                .resolve("src/main/resources/cellsociety/view/Screen/MainScreen.css").toString());
        try {
            scene.getStylesheets().add(cssFile.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        stage.setTitle("Launcher");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void findTheme() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(DEFAULT_THEME, Arrays.asList(langChoices));
        dialog.setTitle("Theming Cell Society");
        dialog.setHeaderText("Theme Setting");
        dialog.setContentText("Choose your theme:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(choice -> setTheme(choice), () -> setTheme(DEFAULT_THEME));
    }

    private void setTheme(String theme) {
        chosenTheme = theme;
    }

    public String getLanguage() {
        return languageView.getLanguage();
    }
}
