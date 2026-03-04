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

  /**
   * Creates a new {@link Button} whose label text is looked up from the
   * Buttons resource bundle using {@code name} as the key, then registers it
   * under that name for later retrieval.
   *
   * @param name the bundle key and internal identifier for the button
   */
  public void addEmptyButton(String name) {
    Button newButton = new Button();
    buttonHolder.put(name, newButton);
    setButtonText(name, name);
  }

  /**
   * Attaches an action handler to the button registered under {@code name}.
   *
   * @param name the identifier of the target button
   * @param e    the handler to invoke when the button is clicked
   */
  public void addActionToButton(String name, EventHandler<ActionEvent> e) {
    getButton(name).setOnAction(e);
  }

  /**
   * Returns the {@link Button} registered under {@code name}, or {@code null}
   * if no such button exists.
   *
   * @param name the identifier of the button to retrieve
   * @return the corresponding {@link Button}
   */
  public Button getButton(String name) {
    return buttonHolder.get(name);
  }

  /**
   * Updates the label of the button registered under {@code name} using
   * {@code text} as a resource-bundle key. Falls back to using {@code text}
   * literally if the key is not found in the bundle.
   *
   * @param name the identifier of the button to update
   * @param text the bundle key (or fallback literal) for the new label
   */
  public void setButtonText(String name, String text) {
    try {
      getButton(name).setText(myResources.getString(text));
    } catch (MissingResourceException e) {
      LOG.warn("Failed to find button text for key: {}. Defaulting to key as text.", text, e);
      getButton(name).setText(text);
    }
  }
}
