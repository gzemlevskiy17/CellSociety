package cellsociety.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Singleton class that manages the application's visual theme state.
 *
 * <p><b>Purpose:</b> Maintains the current active theme and provides a subscription mechanism
 * to notify UI components when the theme changes.</p>
 *
 * <p><b>Usage:</b> Access the singleton via {@link #getInstance()}, register listeners using
 * {@link #addListener(Consumer)}, and switch themes with {@link #setTheme(String)}.</p>
 *
 * <p><b>Justification:</b> Using a Singleton ensures a single source of truth for the theme
 * across the entire application, while the Observer pattern decouples the theme selection logic
 * from the specific views that need to update their appearance.</p>
 *
 * @author Georgiy Zemlevskiy
 */
public class ThemeManager {

  private static ThemeManager instance;
  private static String DEFAULT_THEME = "Light";
  private static String currentTheme;
  private List<Consumer<String>> listeners = new ArrayList<>();

  //TODO: rename to design pattern
  private ThemeManager() {
  }

  public static ThemeManager getInstance() {
    if (instance == null) {
      instance = new ThemeManager();
      currentTheme = DEFAULT_THEME;
    }
    return instance;
  }

  public String getTheme() {
    return currentTheme;
  }

  public void setTheme(String theme) {
    currentTheme = theme;
    notifyListeners();
  }

  public void addListener(Consumer<String> listener) {
    listeners.add(listener);
  }

  public void removeListener(Consumer<String> listener) {
    listeners.remove(listener);
  }

  private void notifyListeners() {
    for (Consumer<String> listener : listeners) {
      listener.accept(currentTheme);
    }
  }

  public String getDefaultTheme() {
    return DEFAULT_THEME;
  }

  public void setDefaultTheme(String theme) {
    DEFAULT_THEME = theme;
  }
}
