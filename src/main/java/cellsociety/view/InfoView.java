package cellsociety.view;

import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InfoView {
    private Stage newStage;
    private Button closing;

    public InfoView(Stage ownerStage) {
        newStage = new Stage();
        newStage.initOwner(ownerStage);
        makeActionEvent();
    }

    private void makeActionEvent() {
        closing = new Button("OK");
        closing.setOnAction(event -> {
            newStage.close();
        });
    }

    public void showInfoView(Map<String, String> params) {
        VBox stackOfParam = new VBox(5);
        for (String paramName : params.keySet()) {
            HBox values = new HBox(5);
            values.getChildren().add(new Label(paramName));
            Label text = new Label(params.get(paramName));
            text.setWrapText(true);
            text.setPrefWidth(100);
            ScrollPane scrolling = new ScrollPane(text);
            values.getChildren().add(scrolling);
            stackOfParam.getChildren().add(values);
        }

        stackOfParam.getChildren().add(closing);
        stackOfParam.setAlignment(Pos.CENTER);
        stackOfParam.setPadding(new Insets(10));
        Scene infoScene = new Scene(stackOfParam);
        newStage.setScene(infoScene);
        newStage.show();
    }

    public void showInfoEdit() {

    }
}
