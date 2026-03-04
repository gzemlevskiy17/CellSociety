package cellsociety.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LanguageManager {

  private static LanguageManager instance;
  private static Locale DEFAULT_LOCALE = Locale.getDefault();
  private static Locale currentLocale;
  private List<Consumer<Locale>> listeners = new ArrayList<>();

  //TODO: rename to design pattern
  private LanguageManager() {
  }

  public static LanguageManager getInstance() {
    if (instance == null) {
      instance = new LanguageManager();
      currentLocale = DEFAULT_LOCALE;
    }
    return instance;
  }

  public Locale getLocale() {
    return currentLocale;
  }

  public void setLocale(Locale locale) {
    currentLocale = locale;
    notifyListeners();
  }

  public void addListener(Consumer<Locale> listener) {
    listeners.add(listener);
  }

  public void removeListener(Consumer<Locale> listener) {
    listeners.remove(listener);
  }

  private void notifyListeners() {
    for (Consumer<Locale> listener : listeners) {
      listener.accept(currentLocale);
    }
  }

  public String getDefaultLanguage() {
    return DEFAULT_LOCALE.getDisplayLanguage();
  }

  public Locale getDefaultLocale() {
    return DEFAULT_LOCALE;
  }

  public void setDefaultLocale(Locale defaultLocale) {
    DEFAULT_LOCALE = defaultLocale;
  }
}
