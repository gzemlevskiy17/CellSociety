package cellsociety.view;

import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StyleLoader {

  public static final String DEFAULT_RESOURCE_PACKAGE = StyleLoader.class.getPackageName() + ".";
  public static final String DEFAULT_STYLESHEET_PATH =
      "/" + DEFAULT_RESOURCE_PACKAGE.replace(".", "/") + "Style/";
  public static final String LAYOUT_STYLESHEET_PATH = DEFAULT_STYLESHEET_PATH + "Layout/";
  public static final String THEME_STYLESHEET_PATH = DEFAULT_STYLESHEET_PATH + "Theme/";
  private static final Logger LOG = LogManager.getLogger(StyleLoader.class);

  public void addThemeStyleSheet(Scene scene, String theme) {
    scene.getStylesheets().clear();
    addStyleSheet(scene, THEME_STYLESHEET_PATH + theme + ".css");
  }

  public void addStyleSheets(Scene scene, String theme, String calleeClassName) {
    addThemeStyleSheet(scene, theme);
    addLayoutStyleSheet(scene, calleeClassName);
  }

  public void addLayoutStyleSheet(Scene scene, String calleeClassName) {
    addStyleSheet(scene, LAYOUT_STYLESHEET_PATH + calleeClassName + ".css");
  }

  private void addStyleSheet(Scene scene, String styleSheetPath) {
    String importedStyleSheet = null;
    try {
      importedStyleSheet = getClass().getResource(
              styleSheetPath)
          .toExternalForm();
    } catch (NullPointerException e) {
      String message = String.format("Failed to load layout stylesheet from %s.",
          styleSheetPath);
      LOG.warn(message, e);
      throw new RuntimeException(message, e);
    }

    scene.getStylesheets().add(importedStyleSheet);
  }
}
