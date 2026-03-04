package cellsociety.view;

import javafx.scene.control.Alert;

/**
 * Displays a modal error alert dialog to the user.
 *
 * @author cellsociety team
 */
public class ErrorView {
    public ErrorView(String title, String message) {
        showError(title, message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
