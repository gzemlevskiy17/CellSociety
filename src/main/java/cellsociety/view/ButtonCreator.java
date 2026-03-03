package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ButtonCreator {

    private static final Logger LOG = LogManager.getLogger(ButtonCreator.class);
    private LinkedHashMap<String, Button> buttonHolder;
    private ResourceBundle myResources;

    public ButtonCreator(Locale locale) {
        buttonHolder = new LinkedHashMap<>();
        try {
            myResources = PropertiesLoader.getResourceBundleWithLocale("view/Text/Buttons/Buttons",
                    locale);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load button properties for language: " + locale.getDisplayLanguage(), e);
        }
    }

    // TODO: fix confusing method name. why is the button empty?
    public void addEmptyButton(String name) {
        Button newButton = new Button();
        buttonHolder.put(name, newButton);
        setButtonText(name, name);
    }

    public void addActionToButton(String name, EventHandler<ActionEvent> e) {
        getButton(name).setOnAction(e);
    }

    public Button getButton(String name) {
        return buttonHolder.get(name);
    }

    public void setButtonText(String name, String text) {
        try {
            getButton(name).setText(myResources.getString(text));
        } catch (MissingResourceException e) {
            LOG.warn("Failed to find button text for key: {}. Defaulting to key as text.", text, e);
            getButton(name).setText(text);
        }
    }

    public void changeLocale(Locale locale) { // resource bundle does not work, rewrite
        try {
            myResources = PropertiesLoader.getResourceBundleWithLocale("view/Text/Buttons/Buttons",
                    locale);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load button properties for language: " + locale.getDisplayLanguage(), e);
        }
        for (String name : buttonHolder.keySet()) {
            setButtonText(name, name);
        }
    }
}
