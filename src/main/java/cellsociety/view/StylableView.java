package cellsociety.view;

import java.util.Locale;
import java.util.function.Consumer;
import javafx.scene.Scene;

/**
 * Abstract base class for UI components that need to respond to dynamic styling and localization
 * changes.
 *
 * <p><b>Purpose:</b> Centralizes the logic for registering and unregistering listeners with the
 * {@link ThemeManager} and {@link LanguageManager}, ensuring that all views update
 * consistently.</p>
 *
 * <p><b>Usage:</b> Extend this class and call {@link #registerThemeListener(Scene)} and/or
 * {@link #registerLanguageListener()} in the constructor. Implementing subclasses must define
 * {@link #onLanguageChanged(Locale)} to handle text updates.</p>
 *
 * <p><b>Justification:</b> Reduces code duplication (DRY) by pulling common event subscription
 * logic
 * up into a single parent class, making it easier to maintain theme/language switching across the
 * application.</p>
 *
 * @author Georgiy Zemlevskiy
 */
public abstract class StylableView {

  protected final ThemeManager themeManager;
  protected final LanguageManager languageManager;
  protected final StyleLoader styleLoader;

  private Consumer<String> themeListener;
  private Consumer<Locale> languageListener;

  protected StylableView() {
    this.themeManager = ThemeManager.getInstance();
    this.languageManager = LanguageManager.getInstance();
    this.styleLoader = new StyleLoader();
  }

  protected void registerThemeListenerWithLayoutSheet(Scene scene) {
    String className = this.getClass().getSimpleName();
    themeListener = theme -> styleLoader.addStyleSheets(scene, theme, className);
    themeManager.addListener(themeListener);
    styleLoader.addStyleSheets(scene, themeManager.getTheme(), className);
  }

  protected void registerThemeListener(Scene scene) {
    themeListener = theme -> styleLoader.addThemeStyleSheet(scene, theme);
    themeManager.addListener(themeListener);
    // Apply current values
    styleLoader.addThemeStyleSheet(scene, themeManager.getTheme());
  }

  protected void registerLanguageListener() {
    languageListener = locale -> onLanguageChanged(locale);
    languageManager.addListener(languageListener);
    // Apply current values
    onLanguageChanged(languageManager.getLocale());
  }

  protected void unregisterThemeListener() {
    if (themeListener != null) {
      themeManager.removeListener(themeListener);
      themeListener = null;
    }
  }

  protected void unregisterLanguageListener() {
    if (languageListener != null) {
      languageManager.removeListener(languageListener);
      languageListener = null;
    }
  }

  protected void unregisterAllListeners() {
    unregisterThemeListener();
    unregisterLanguageListener();

  }

  protected abstract void onLanguageChanged(Locale locale);
}
