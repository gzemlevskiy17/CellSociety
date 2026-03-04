package cellsociety.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
