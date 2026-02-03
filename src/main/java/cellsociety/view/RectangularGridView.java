package cellsociety.view;

import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RectangularGridView extends GridView {

    public RectangularGridView(Game gameToDisplay) {
        super(gameToDisplay);
    }

    @Override
    public Scene render() {
        GridPane grid = new GridPane();
        return new Scene(grid);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Grid");
        stage.setScene(render());
        stage.show();
    }

}
