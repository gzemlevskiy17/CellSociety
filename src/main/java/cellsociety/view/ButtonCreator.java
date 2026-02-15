package cellsociety.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ButtonCreator {
    private int size;
    private ArrayList<Button> buttonHolder;
    private HashMap<String, Integer> buttonIndex;
    private ResourceBundle myResources;

    public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety.view.";
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + DEFAULT_RESOURCE_PACKAGE.replace(".", "/");

    public ButtonCreator(String language) {
        size = 0;
        buttonHolder = new ArrayList<>();
        buttonIndex = new HashMap<>();
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language + "Buttons"); //TODO: complete default text setting implementation
    }

    //TODO: fix confusing method name. why is the button empty?
    public void addEmptyButton(String name) {
        Button newButton = new Button(myResources.getString(name));
        buttonHolder.add(size, newButton);
        buttonIndex.put(name, size);
        size++;
    }

    public void addActionToButton(String name, EventHandler<ActionEvent> e) {
        getButton(name).setOnAction(e);
    }

    public Button getButton(String name) {
        return buttonHolder.get(buttonIndex.get(name));
    }

    public void setButtonText(String name, String text) {
        getButton(name).setText(myResources.getString(text));
    }

    public Iterator<Button> returnAll() {
        return buttonHolder.iterator();
    }
}
