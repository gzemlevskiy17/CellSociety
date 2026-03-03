package cellsociety.controller;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesLoader {

    public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety/view/"; //TODO: parameterize
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + DEFAULT_RESOURCE_PACKAGE;
    private static final Logger LOG = LogManager.getLogger(PropertiesLoader.class);

    public static ResourceBundle getResourceBundle(String fileName) {
        return loadBundle(fileName,
            () -> ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + fileName));
    }

    public static ResourceBundle getResourceBundleWithLocale(String fileName, Locale locale) {
        return loadBundle(fileName,
            () -> ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + fileName, locale));
    }

    private static ResourceBundle loadBundle(String fileName, Supplier<ResourceBundle> loader) {
        try {
            return loader.get();
        } catch (NullPointerException | MissingResourceException e) {
            String message = String.format("Failed to load properties from file: %s.",
                fileName);
            LOG.warn(message, e);
            throw new NullPointerException(message);
        }
    }
}
