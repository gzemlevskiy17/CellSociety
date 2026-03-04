package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import cellsociety.model.ParseResult;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Displays and edits simulation metadata and parameters in a secondary window.
 *
 * <p>Provides two modes: a read-only info view ({@link #showInfoView}) and an
 * editable form ({@link #showInfoEdit}) whose results are collected via
 * {@link #getEditResults()}. Parameter spinners are configured from the game's
 * {@code model/[GameType].properties} resource file.</p>
 *
 * @author cellsociety team
 */

public class InfoView extends StylableView {
  private static final Logger LOG = LogManager.getLogger(InfoView.class);
  private Stage newStage;
  private ParseResult currentResult;

  private Class<?> clazz;
  private Class<?>[] paramTypes = new Class<?>[]{};
  private String[] buttonNamesView = {"OK"};
  private String[] buttonNamesEdit = {"Save", "Cancel"};
  private String[] infoSolid = {"GameType", "Rows", "Cols"};
  private String[] infoSearch = {"Title", "Author", "Description"};
  private String[] paramVariables = {};
  private Map<String, Spinner<Integer>> intChanges;
  private Map<String, Spinner<Double>> doubleChanges;
  private Map<String, TextField> textChanges;
  private Map<Integer, ColorPicker> colorChanges;
  private ButtonCreator buttonHold;
  private ResourceBundle screenText;
  private ResourceBundle config;

  private ResourceBundle gameResources;

  public InfoView(Stage ownerStage, ParseResult givenInfo, Runnable save) {
        newStage = new Stage();
        newStage.initOwner(ownerStage);
        currentResult = givenInfo;
        clazz = currentResult.getClass();
        buttonHold = new ButtonCreator();
        try {
          gameResources = PropertiesLoader.getResourceBundle(
              "model/" + currentResult.getGameType());
          String variablesToExplore = gameResources.getString("variables");
          if (!variablesToExplore.isBlank()) {
            paramVariables = variablesToExplore.split("[,\s]+");
            }
        } catch (Exception e) {
            new ErrorView("Missing Variables", "No Properties File for " + currentResult.getGameType());
        }
        setUpChanges();
        makeActionEvent(save);
    }

  private void setUpChanges() {
    intChanges = new HashMap<>();
    doubleChanges = new HashMap<>();
    textChanges = new HashMap<>();
    colorChanges = new HashMap<>();
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
            Spinner<Integer> spinner = new Spinner<>();
            int init = Integer.parseInt(currentResult.getParam(name));
            int minim = Integer.parseInt(gameResources.getString(name + ".min"));
            int maxim = Integer.parseInt(gameResources.getString(name + ".max"));
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                minim, maxim, init, 1);
            spinner.setValueFactory(valueFactory);

            intChanges.put(name, spinner);
          } catch (Exception e) {
            LOG.warn("Could not create int spinner for param '{}', falling back to text field", name, e);
            textChanges.put(name, new TextField());
          }
          break;
        case "double":
          try {
            Spinner<Double> spinner = new Spinner<>();
            double init = Double.parseDouble(currentResult.getParam(name));
            double minim = Double.parseDouble(gameResources.getString(name + ".min"));
            double maxim = Double.parseDouble(gameResources.getString(name + ".max"));
            SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(
                minim, maxim,
                init);
            spinner.setValueFactory(valueFactory);

            doubleChanges.put(name, spinner);
          } catch (Exception e) {
            LOG.warn("Could not create double spinner for param '{}', falling back to text field", name, e);
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

  private void makeActionEvent(Runnable save) {
    for (String name : buttonNamesView) {
      buttonHold.addEmptyButton(name);
    }
    for (String name : buttonNamesEdit) {
      buttonHold.addEmptyButton(name);
    }
    buttonHold.addActionToButton("OK", event -> {
      newStage.close();
    });
    buttonHold.addActionToButton("Save", event -> {
      save.run();
    });
    buttonHold.addActionToButton("Cancel", event -> {
      newStage.close();
    });
  }

  public void showInfoView(ParseResult givenInfo) {
    currentResult = givenInfo;
    clazz = currentResult.getClass();
    VBox stackOfParam = createTextArea(false);
    stackOfParam.setId("info-stack");
    stackOfParam.getChildren().addAll(createParamArea(false));
    stackOfParam.getChildren().addAll(createColorArea(false));
    setupInfoButton(stackOfParam, buttonNamesView);
    //        registerLanguageListener();
    registerThemeListenerWithLayoutSheet(newStage.getScene());

    newStage.setOnHidden(event -> unregisterAllListeners());
    newStage.setTitle("Show Information Screen");
    newStage.show();
  }

  private void setupInfoButton(VBox stackOfParameters, String[] buttonNamesView) {
    HBox buttonArea = new HBox();
    buttonArea.setId("button-area");
    for (String name : buttonNamesView) {
      buttonArea.getChildren().add(buttonHold.getButton(name));
    }
    stackOfParameters.getChildren().add(buttonArea);
    Scene infoScene = new Scene(stackOfParameters);
    newStage.setScene(infoScene);
  }

  public void showInfoEdit(ParseResult givenInfo) {
    currentResult = givenInfo;
    clazz = currentResult.getClass();
    VBox stackOfParam = createTextArea(true);
    stackOfParam.getChildren().addAll(createParamArea(true));
    stackOfParam.getChildren().addAll(createColorArea(true));
    setupInfoButton(stackOfParam, buttonNamesEdit);
    //        registerLanguageListener();
    registerThemeListenerWithLayoutSheet(newStage.getScene());

    newStage.setOnHidden(event -> unregisterAllListeners());
    newStage.setTitle("Show Edit Screen"); //TODO: properties
    newStage.show();
  }

  public VBox createTextArea(boolean allowEditing) {
    VBox stackOfParam = new VBox();
    stackOfParam.setId("param-area");
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
    HBox values = new HBox();
    values.setId("values");
    values.getChildren().add(new Label(paramName));

    String result = "";
    try {
      result =
          "" + clazz.getMethod("get" + paramName, paramTypes).invoke(currentResult, paramTypes);
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
      text.prefWidth(200); //TODO: properties
    }
    ScrollPane scrolling = new ScrollPane((Node) text);
    values.getChildren().add(scrolling);
    return values;
  }

  private VBox createParamArea(boolean allowEditing) {
    VBox stackOfParam = new VBox();
    stackOfParam.setId("param-area");

    for (String param : paramVariables) {
      HBox values = new HBox();
      values.setId("values");
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
        textOnly.prefWidth(200); //TODO: properties

        ScrollPane scrolling = new ScrollPane(textOnly);
        values.getChildren().add(scrolling);
      }
      stackOfParam.getChildren().add(values);
    }
    return stackOfParam;
  }

  private VBox createColorArea(boolean allowEditing) {
    VBox stackOfParam = new VBox();
    stackOfParam.setId("param-area");
    Set<Integer> colorKeys = currentResult.getColorKeys();

    for (Integer state : colorKeys) {
      HBox values = new HBox(5);
      values.setId("values");
      values.getChildren().add(new Label("State" + state + ":"));

      Node text = null;
      String currentColor = currentResult.getColor(state);
      if (allowEditing) {
        text = colorChanges.get(state);
        ((ColorPicker) text).setValue(Color.web(currentColor));
      } else {
        Rectangle colorPatch = new Rectangle(25, 25, Color.web(currentColor)); //TODO: properties
        values.getChildren().add(colorPatch);
        text = new Label(currentColor);
        ((Labeled) text).setWrapText(true);
        text.prefWidth(200); //TODO: properties
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
        Map<String, String> params = new HashMap<>(currentResult.getParams());
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
        for (String name : paramVariables) {
            if (intChanges.containsKey(name)) {
                try {
                    params.put(name, Integer.toString(intChanges.get(name).getValue()));
                } catch (Exception e) {
                }
            }
            if (doubleChanges.containsKey(name)) {
                try {
                    params.put(name, Double.toString(doubleChanges.get(name).getValue()));
                } catch (Exception e) {
                }
            }
            if (textChanges.containsKey(name)) {
                try {
                    params.put(name, textChanges.get(name).getText());
                } catch (Exception e) {
                }
            }
        }

    return new ParseResult(rows, cols, gameType, title, author, description, cells, params,
        colorMap);
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    screenText = PropertiesLoader.getResourceBundleWithLocale(
        "view/Text/Screen/GameScreen/InfoView/InfoView", locale);
  }
}
