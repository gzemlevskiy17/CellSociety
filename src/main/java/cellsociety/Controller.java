package cellsociety;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.view.GridView;
import cellsociety.view.RectangularGridView;
import cellsociety.view.ScreenManager;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class Controller {

  private Game game;
  private ScreenManager screenManager;
  private GridView gridView;
  private Timeline gameLoop;
  private Timeline animation;

  private static final int FRAMES_PER_SECOND = 60;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private boolean running;
  private double gameSpeed;
  private int generations;

  public Controller(Game game, ScreenManager screenManager, GridView gridView) {
    this.game = game;
    this.screenManager = screenManager;
    this.gridView = gridView;
    gameSpeed = 1;
    gameLoop = new Timeline(new KeyFrame(Duration.seconds(2), e -> loop()));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
    System.out.println("True");
  }

  public void loop() {
    game.step();
    updateGrid();
    generations++;
  }

  private void updateGrid() {
    int rows = gridView.getNumRows();
    int cols = gridView.getNumCols();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = game.getCell(r, c);
        gridView.updateCell(r, c, cell.getState());
      }
    }
  }

  public void pause() {
    gameLoop.stop();
    running = false;
  }

  public void play() {
    running = true;
  }

  public void reset() {
    // game.defaultInitializeGrid(); //TODO: method call awaits implementation
    generations = 0;
    running = false;
    gameSpeed = 1.0;
  }

  public void edit() {
  }

  public void writeConfigFile(/* String path */) {
  }

  public double getGameSpeed() {
    return gameSpeed;
  }

  public void setGameSpeed(double gameSpeed) {
    this.gameSpeed = gameSpeed;
    if (animation != null) {
      animation.setRate(this.gameSpeed);
    }
  }

  public int getGenerations() {
    return generations;
  }

  public void setGenerations(int generations) {
    this.generations = generations;
  }
}
