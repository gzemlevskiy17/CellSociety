package cellsociety;

import cellsociety.model.Game;
import cellsociety.view.ScreenManager;

public class Controller {

  private Game game;
  private ScreenManager screenManager;

  private boolean running;
  private double gameSpeed;
  private int generations;

  public Controller(Game game, ScreenManager screenManager) {
    this.game = game;
    this.screenManager = screenManager;
    gameSpeed = 1;
  }

  public void loop() {
    if (running) {
//    game.updateGame();
//    screenManager.updateScreen();
      //TODO: method calls await implementation
      //TODO: outsource methods
      generations++;
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
