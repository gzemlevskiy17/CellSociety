package cellsociety.controller;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class PropertiesLoader {
    public static final String DEFAULT_RESOURCE_PACKAGE = "cellsociety/view/";
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + DEFAULT_RESOURCE_PACKAGE;

    public static ResourceBundle loadProperties(String fileName) throws Exception {
        // crashes if Locale is not changed
        try {
            return ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + fileName);
        } catch (NullPointerException | MissingResourceException e) {
            throw new NullPointerException("Property File does not exist");
        }
    }
}
