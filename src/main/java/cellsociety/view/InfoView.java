package cellsociety.view;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cellsociety.model.ParseResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class InfoView {
    private Stage newStage;
    private ParseResult currentResult;
    private String[] buttonNamesView = { "OK" };
    private String[] buttonNamesEdit = { "Save", "Cancel" };
    private String[] infoSearch = { "GameType", "Title", "Author", "Description" };
    private HashMap<String, Spinner<?>> valueChanges;
    private HashMap<String, TextField> textChanges;
    private HashMap<Integer, ColorPicker> colorChanges;
    private ButtonCreator buttonHold;
    private String currentLang;

    public InfoView(Stage ownerStage, ParseResult givenInfo, String language) {
        newStage = new Stage();
        newStage.initOwner(ownerStage);
        currentResult = givenInfo;
        currentLang = language;
        buttonHold = new ButtonCreator(currentLang);
        setUpChanges();
        makeActionEvent();
    }

    private void setUpChanges() {
        valueChanges = new HashMap<String, Spinner<?>>();
        textChanges = new HashMap<String, TextField>();
        colorChanges = new HashMap<Integer, ColorPicker>();
        for (String name : infoSearch) {
            textChanges.put(name, new TextField());
        }
        Set<String> params = currentResult.getParamKeys();
        for (String name : params) {
            try {
                Spinner<Double> spinner = new Spinner<>();
                double init = Double.parseDouble(currentResult.getParam(name));
                SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0,
                        init, 0.1);
                spinner.setValueFactory(valueFactory);

                valueChanges.put(name, spinner);
            } catch (Exception e) {
                textChanges.put(name, new TextField());
            }
        }
        Set<Integer> colors = currentResult.getColorKeys();
        for (Integer state : colors) {
            colorChanges.put(state, new ColorPicker());
        }
    }

    private void makeActionEvent() {
        for (String name : buttonNamesView) {
            buttonHold.addEmptyButton(name);
        }
        for (String name : buttonNamesEdit) {
            buttonHold.addEmptyButton(name);
        }
        buttonHold.addActionToButton("OK", event -> {
            newStage.close();
        });
    }

    public void showInfoView() {
        VBox stackOfParam = createTextArea(false);
        stackOfParam.getChildren().addAll(createParamArea(false));
        stackOfParam.getChildren().addAll(createColorArea(false));
        HBox buttonArea = new HBox(10);
        for (String name : buttonNamesView) {
            buttonArea.getChildren().add(buttonHold.getButton(name));
        }
        stackOfParam.getChildren().add(buttonArea);
        stackOfParam.setAlignment(Pos.CENTER);
        stackOfParam.setPadding(new Insets(10));
        Scene infoScene = new Scene(stackOfParam);
        newStage.setScene(infoScene);
        newStage.setTitle("Show Information Screen");
        newStage.show();
    }

    public void showInfoEdit() {

    }

    public VBox createTextArea(boolean allowEditing) {
        VBox stackOfParam = new VBox(5);
        Class<?> clazz = currentResult.getClass();
        Class<?>[] paramTypes = new Class<?>[] {};
        for (String paramName : infoSearch) {
            HBox values = new HBox(5);
            values.getChildren().add(new Label(paramName));

            String result = "";
            try {
                result = (String) clazz.getMethod("get" + paramName, paramTypes).invoke(currentResult, paramTypes);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                allowEditing = false;
            }
            Node text = null;
            if (allowEditing) {
                text = textChanges.get(paramName);
                ((TextField) text).setText(result);
            } else {
                text = new Label(result);
                ((Labeled) text).setWrapText(true);
                text.prefWidth(200);
            }
            ScrollPane scrolling = new ScrollPane((Node) text);
            values.getChildren().add(scrolling);
            stackOfParam.getChildren().add(values);
        }
        return stackOfParam;
    }

    private VBox createParamArea(boolean allowEditing) {
        VBox stackOfParam = new VBox(5);
        Set<String> paramKeys = currentResult.getParamKeys();

        for (String param : paramKeys) {
            HBox values = new HBox(5);
            values.getChildren().add(new Label(param));

            Node text = null;
            if (allowEditing) {
                if (valueChanges.containsKey(param)) {
                    text = valueChanges.get(param);
                    ((Spinner<Double>) text).getValueFactory()
                            .setValue(Double.parseDouble(currentResult.getParam(param)));
                } else {
                    text = textChanges.get(param);
                    ((TextField) text).setText(currentResult.getParam(param));
                }
            } else {
                text = new Label(currentResult.getParam(param));
                ((Labeled) text).setWrapText(true);
                text.prefWidth(200);
            }
            ScrollPane scrolling = new ScrollPane((Node) text);
            values.getChildren().add(scrolling);
            stackOfParam.getChildren().add(values);
        }
        return stackOfParam;
    }

    private VBox createColorArea(boolean allowEditing) {
        VBox stackOfParam = new VBox(5);
        Set<Integer> colorKeys = currentResult.getColorKeys();

        for (Integer state : colorKeys) {
            HBox values = new HBox(5);
            values.getChildren().add(new Label("State" + state + ":"));

            Node text = null;
            if (allowEditing) {
                text = colorChanges.get(state);
                ((ColorPicker) text).setValue(Color.web(currentResult.getColor(state)));
            } else {
                text = new Label(currentResult.getColor(state));
                ((Labeled) text).setWrapText(true);
                text.prefWidth(200);
            }
            ScrollPane scrolling = new ScrollPane((Node) text);
            values.getChildren().add(scrolling);
            stackOfParam.getChildren().add(values);
        }
        return stackOfParam;
    }

    public void changeLang(String language) {
        currentLang = language;
        buttonHold.changeLanguage(currentLang);
    }
}
