package cellsociety.view;

import cellsociety.controller.PropertiesLoader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates and manages labeled {@link javafx.scene.control.Button} instances with
 * locale-aware text loaded from resource bundles.
 *
 * <p>Buttons are stored by name and can be retrieved, re-labeled, or have actions
 * attached after creation. Text is resolved via the {@code view/Text/Buttons/Buttons}
 * properties file for the current locale.</p>
 *
 * @author cellsociety team
 */
public class ButtonCreator extends StylableView {

    private static final Logger LOG = LogManager.getLogger(ButtonCreator.class);
    private Map<String, Button> buttonHolder;
    private ResourceBundle myResources;


  public ButtonCreator() {
    buttonHolder = new LinkedHashMap<>();
    registerLanguageListener();
  }

  /**
   * @param locale
   */
  @Override
  protected void onLanguageChanged(Locale locale) {
    myResources = PropertiesLoader.getResourceBundleWithLocale("view/Text/Buttons/Buttons",
        locale);
    for (String name : buttonHolder.keySet()) {
      setButtonText(name, name);
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
}
