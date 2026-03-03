package cellsociety.controller;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesLoader {
    public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety/view/";
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + DEFAULT_RESOURCE_PACKAGE;
    private static final Logger LOG = LogManager.getLogger(PropertiesLoader.class);

    public static ResourceBundle getResourceBundle(String fileName) {
        try {
            return ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + fileName);
        } catch (NullPointerException | MissingResourceException e) {
            String message = String.format("Failed to load properties from file: %s.",
                DEFAULT_RESOURCE_FOLDER + fileName);
            LOG.warn(message, e);
            throw new NullPointerException(message);
        }
    }

    public static ResourceBundle getResourceBundleWithLocale(String fileName, Locale locale) {
        try {
            return ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + fileName, locale);
        } catch (NullPointerException | MissingResourceException e) {
            String message = String.format("Failed to load properties from file: %s.",
                DEFAULT_RESOURCE_FOLDER + fileName);
            LOG.warn(message, e);
            throw new NullPointerException(message);
        }
    }
}
