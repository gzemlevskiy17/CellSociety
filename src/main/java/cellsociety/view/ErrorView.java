package cellsociety.view;

import java.util.Locale;
import javafx.scene.control.Alert;

/**
 * Displays a modal error alert dialog to the user.
 *
 * @author cellsociety team
 */
public class ErrorView extends StylableView {

  public ErrorView(String title, String message) {
    showError(title, message);
  }

  private void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
        registerThemeListener(alert.getDialogPane().getScene());
        alert.setOnHidden(event -> unregisterAllListeners());
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

    /**
     * @param locale
     */
    @Override
    protected void onLanguageChanged(Locale locale) {

    }
}
