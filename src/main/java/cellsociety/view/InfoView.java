package cellsociety.view;

import cellsociety.model.ParseResult;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class InfoView {
    private Stage newStage;
    private ParseResult currentResult;

    private Class<?> clazz;
    private Class<?>[] paramTypes = new Class<?>[] {};
    private String[] buttonNamesView = { "OK" };
    private String[] buttonNamesEdit = { "Save", "Cancel" };
    private String[] infoSolid = { "GameType", "Rows", "Cols" };
    private String[] infoSearch = { "Title", "Author", "Description" };
    private HashMap<String, Spinner<?>> valueChanges;
    private HashMap<String, TextField> textChanges;
    private HashMap<Integer, ColorPicker> colorChanges;
    private ButtonCreator buttonHold;
    private Locale locale;

    public InfoView(Stage ownerStage, ParseResult givenInfo, Locale locale) {
        newStage = new Stage();
        newStage.initOwner(ownerStage);
        currentResult = givenInfo;
        clazz = currentResult.getClass();
        this.locale = locale;
        buttonHold = new ButtonCreator(this.locale);
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
        clazz = currentResult.getClass();
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
        clazz = currentResult.getClass();
        VBox stackOfParam = createTextArea(true);
        stackOfParam.getChildren().addAll(createParamArea(true));
        stackOfParam.getChildren().addAll(createColorArea(true));
        HBox buttonArea = new HBox(10);
        for (String name : buttonNamesEdit) {
            buttonArea.getChildren().add(buttonHold.getButton(name));
        }
        stackOfParam.getChildren().add(buttonArea);
        stackOfParam.setAlignment(Pos.CENTER);
        stackOfParam.setPadding(new Insets(10));
        Scene editScene = new Scene(stackOfParam);
        newStage.setScene(editScene);
        newStage.setTitle("Show Edit Screen");
        newStage.show();
    }

    public VBox createTextArea(boolean allowEditing) {
        VBox stackOfParam = new VBox(5);
        for (String paramName : infoSolid) {
            HBox values = returnText(paramName, false);
            stackOfParam.getChildren().add(values);
        }
        for (String paramName : infoSearch) {
            HBox values = returnText(paramName, allowEditing);
            stackOfParam.getChildren().add(values);
        }
        return stackOfParam;
    }

    private HBox returnText(String paramName, boolean allowEditing) {
        HBox values = new HBox(5);
        values.getChildren().add(new Label(paramName));

        String result = "";
        try {
            result = "" + clazz.getMethod("get" + paramName, paramTypes).invoke(currentResult, paramTypes);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
        return values;
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

    private VBox createColorArea(boolean allowEditing) { // TODO: value of parameter is always false
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

    public ParseResult getEditResults() {
        int rows = currentResult.getRows();
        int cols = currentResult.getCols();
        String gameType = currentResult.getGameType();
        String title = currentResult.getTitle();
        String author = currentResult.getAuthor();
        String description = currentResult.getDescription();
        int[][] cells = currentResult.getCells();
        Map<String, String> params = currentResult.getParams();
        Map<Integer, String> colorMap = currentResult.getColorMap();
        return new ParseResult(0, 0, null,
                null, null, null, null,
                null, null);
    }

    public void changeLocale(Locale locale) {
        this.locale = locale;
        buttonHold.changeLocale(this.locale);
    }
}
