package cellsociety.view;

import cellsociety.model.Game;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RectangularGridView extends GridView {

    public RectangularGridView(Game gameToDisplay) {
        super(gameToDisplay);
    }

    @Override
    public void render() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene();
        stage.setTitle("Grid");
        stage.setScene(scene);
        stage.show();
    }

}
