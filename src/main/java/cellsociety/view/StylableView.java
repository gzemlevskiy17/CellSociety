package cellsociety.view;

import java.util.Locale;
import java.util.function.Consumer;
import javafx.scene.Scene;

public abstract class StylableView {

  protected final ThemeManager themeManager;
  protected final LanguageManager languageManager;
  protected final StyleLoader styleLoader;

  protected StylableView() {
    this.themeManager = ThemeManager.getInstance();
    this.languageManager = LanguageManager.getInstance();
    this.styleLoader = new StyleLoader();
  }

  protected Consumer<String> registerThemeListenerWithLayoutSheet(Scene scene) {
    String className = this.getClass().getSimpleName();
    Consumer<String> themeListener = theme -> styleLoader.addStyleSheets(scene, theme, className);
    styleLoader.addStyleSheets(scene, themeManager.getTheme(), className);
    return themeListener;
  }

  protected Consumer<String> registerThemeListener(Scene scene) {
    Consumer<String> themeListener = theme -> styleLoader.addThemeStyleSheet(scene, theme);
    themeManager.addListener(themeListener);
    // Apply current values
    styleLoader.addThemeStyleSheet(scene, themeManager.getTheme());
    return themeListener;
  }

  protected void registerLanguageListener() {
    Consumer<Locale> languageListener = locale -> onLanguageChanged(locale);
    languageManager.addListener(languageListener);
    // Apply current values
    onLanguageChanged(languageManager.getLocale());
  }

  protected abstract void onLanguageChanged(Locale locale);

}
