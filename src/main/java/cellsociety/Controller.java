package cellsociety;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.view.GridView;
import cellsociety.view.RectangularGridView;
import cellsociety.view.ScreenManager;

public class Controller {

  private Game game;
  private ScreenManager screenManager;
  private GridView gridView;

  private boolean running;
  private double gameSpeed;
  private int generations;

  public Controller(Game game, ScreenManager screenManager, GridView gridView) {
    this.game = game;
    this.screenManager = screenManager;
    this.gridView = gridView;
    gameSpeed = 1;
  }


  public void loop() {
    if (running) {
      game.step();
      updateGrid();
      //TODO: method calls await implementation
      //TODO: outsource methods
      generations++;
    }
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
    running = false;
  }

  public void play() {
    running = true;
  }

  public void reset() {
//    game.defaultInitializeGrid(); //TODO: method call awaits implementation
    generations = 0;
    running = false;
    gameSpeed = 1.0;
  }

  public void edit() {
  }

  public void writeConfigFile(/*String path*/) {
  }

  public double getGameSpeed() {
    return gameSpeed;
  }

  public void setGameSpeed(double gameSpeed) {
    this.gameSpeed = gameSpeed;
  }

  public int getGenerations() {
    return generations;
  }

  public void setGenerations(int generations) {
    this.generations = generations;
  }
}
