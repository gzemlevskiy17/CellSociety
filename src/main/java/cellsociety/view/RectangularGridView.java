package cellsociety.view;

import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RectangularGridView extends GridView {

    public RectangularGridView(Game gameToDisplay) {
        super(gameToDisplay);
    }

    @Override
    public Scene render() {
        return new Scene();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Grid");
        stage.setScene(render());
        stage.show();
    }

}
