package cellsociety.view;

import cellsociety.controller.PropertiesLoader;

import java.util.LinkedHashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ButtonCreator {
    private LinkedHashMap<String, Button> buttonHolder;
    private ResourceBundle myResources;

    public ButtonCreator(String language) {
        buttonHolder = new LinkedHashMap<>();
        try {
            myResources = PropertiesLoader.loadProperties("Buttons/" + language);
        } catch (Exception e) {
            e.printStackTrace();
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
            getButton(name).setText(text);
        }
    }

    public void changeLanguage(String language) { // resource bundle does not work, rewrite
        try {
            myResources = PropertiesLoader.loadProperties("Buttons/" + language);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String name : buttonHolder.keySet()) {
            setButtonText(name, name);
        }
    }
}
