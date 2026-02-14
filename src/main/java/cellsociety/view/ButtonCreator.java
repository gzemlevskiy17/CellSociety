package cellsociety.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ButtonCreator {
    private int size;
    private ArrayList<Button> buttonHolder;
    private HashMap<String, Integer> buttonIndex;

    public ButtonCreator() {
        size = 0;
        buttonHolder = new ArrayList<Button>();
        buttonIndex = new HashMap<String, Integer>();
    }

    public void addEmptyButton(String name) {
        buttonHolder.add(size, new Button(name));
        buttonIndex.put(name, Integer.valueOf(size));
        size++;
    }

    public void addActionToButton(String name, EventHandler<ActionEvent> e) {
        buttonHolder.get(buttonIndex.get(name).intValue()).setOnAction(e);
    }

    public Button getButton(String name) {
        return buttonHolder.get(buttonIndex.get(name).intValue());
    }

    public Iterator<Button> returnAll() {
        return (Iterator<Button>) buttonHolder.iterator();
    }
}
