package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import cellsociety.model.ParseResult;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.shape.Rectangle;
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
    private String[] paramVariables = {};
    private HashMap<String, Spinner<Integer>> intChanges;
    private HashMap<String, Spinner<Double>> doubleChanges;
    private HashMap<String, TextField> textChanges;
    private HashMap<Integer, ColorPicker> colorChanges;
    private ButtonCreator buttonHold;
    private Locale locale;

    private ResourceBundle gameResources;
    private static final String LAYOUT_STYLESHEET = "";
    private static final String MODEL_RESOURCE_FOLDER = "model/";

    public InfoView(Stage ownerStage, ParseResult givenInfo, Locale locale) {
        newStage = new Stage();
        newStage.initOwner(ownerStage);
        currentResult = givenInfo;
        clazz = currentResult.getClass();
        this.locale = locale;
        buttonHold = new ButtonCreator(this.locale);
        try {
            gameResources = PropertiesLoader.getResourceBundle(MODEL_RESOURCE_FOLDER + currentResult.getGameType());
            paramVariables = gameResources.getString("variables").split("[,\s]+");
        } catch (Exception e) {
            new ErrorView("Missing Variables", "No Properties File for " + currentResult.getGameType());
        }
        setUpChanges();
        makeActionEvent();
    }

    private void setUpChanges() {
        intChanges = new HashMap<String, Spinner<Integer>>();
        doubleChanges = new HashMap<String, Spinner<Double>>();
        textChanges = new HashMap<String, TextField>();
        colorChanges = new HashMap<Integer, ColorPicker>();
        for (String name : infoSearch) {
            textChanges.put(name, new TextField());
        }
        for (String name : paramVariables) {
            switch (gameResources.getString(name + ".type")) {
                case "String":
                    textChanges.put(name, new TextField());
                    break;
                case "int":
                    try {
                        Spinner<Integer> spinner = new Spinner<Integer>();
                        int init = Integer.parseInt(currentResult.getParam(name));
                        int minim = Integer.parseInt(gameResources.getString(name + ".min"));
                        int maxim = Integer.parseInt(gameResources.getString(name + ".max"));
                        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                minim, maxim, init, 1);
                        spinner.setValueFactory(valueFactory);

                        intChanges.put(name, spinner);
                    } catch (Exception e) {
                        textChanges.put(name, new TextField());
                    }
                    break;
                case "double":
                    try {
                        Spinner<Double> spinner = new Spinner<Double>();
                        double init = Double.parseDouble(currentResult.getParam(name));
                        double minim = Double.parseDouble(gameResources.getString(name + ".min"));
                        double maxim = Double.parseDouble(gameResources.getString(name + ".max"));
                        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(
                                minim, maxim,
                                init);
                        spinner.setValueFactory(valueFactory);

                        doubleChanges.put(name, spinner);
                    } catch (Exception e) {
                        textChanges.put(name, new TextField());
                    }
                    break;
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

    public void showInfoView(ParseResult givenInfo) {
        currentResult = givenInfo;
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

    public void showInfoEdit(ParseResult givenInfo) {
        currentResult = givenInfo;
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

        for (String param : paramVariables) {
            HBox values = new HBox(5);
            values.getChildren().add(new Label(param));

            if (allowEditing) {
                switch (gameResources.getString(param + ".type")) {
                    case "String":
                        TextField text = textChanges.get(param);
                        text.setText(currentResult.getParam(param));
                        ScrollPane scrolling = new ScrollPane(text);
                        values.getChildren().add(scrolling);
                        break;
                    case "int":
                        Spinner<Integer> numInt = intChanges.get(param);
                        numInt.getValueFactory()
                                .setValue(Integer.parseInt(currentResult.getParam(param)));

                        values.getChildren().add(numInt);
                        break;
                    case "double":
                        Spinner<Double> numDouble = doubleChanges.get(param);
                        numDouble.getValueFactory()
                                .setValue(Double.parseDouble(currentResult.getParam(param)));

                        values.getChildren().add(numDouble);
                        break;
                }
            } else {
                Label textOnly = new Label(currentResult.getParam(param));
                textOnly.setWrapText(true);
                textOnly.prefWidth(200);

                ScrollPane scrolling = new ScrollPane(textOnly);
                values.getChildren().add(scrolling);
            }
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
            String currentColor = currentResult.getColor(state);
            if (allowEditing) {
                text = colorChanges.get(state);
                ((ColorPicker) text).setValue(Color.web(currentColor));
            } else {
                Rectangle colorPatch = new Rectangle(25, 25, Color.web(currentColor));
                values.getChildren().add(colorPatch);
                text = new Label(currentColor);
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

        try {
            title = textChanges.get("Title").getText();
        } catch (Exception e) {
        }
        try {
            author = textChanges.get("Author").getText();
        } catch (Exception e) {
        }
        try {
            description = textChanges.get("Description").getText();
        } catch (Exception e) {
        }

        return new ParseResult(rows, cols, gameType, title, author, description, cells, params, colorMap);
    }

    public void changeLocale(Locale locale) {
        this.locale = locale;
        buttonHold.changeLocale(this.locale);
    }
}
