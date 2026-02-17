package cellsociety.controller;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.view.GridView;
import cellsociety.view.ScreenManager;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class Controller {

  private Game game;
  private ScreenManager screenManager;
  private GridView gridView;
  private Timeline gameLoop;

  private static final double FRAMES_PER_SECOND = 0.5;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private double gameSpeed;
  private int generations = 0;

  public Controller(Game game, ScreenManager screenManager, GridView gridView, double gameSpeed,
      Runnable createNewGameAction) {
    this.game = game;
    this.screenManager = screenManager;
    this.gridView = gridView;
    screenManager.setGridView(gridView);
    this.gameSpeed = gameSpeed;
    gameLoop = new Timeline(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> loop()));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.pause();
  }

  public void loop() {
    game.step();
    updateGrid();
    generations++;
    screenManager.setGen(generations);
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
    gameLoop.pause();
  }

  public void play() {
    gameLoop.play();
  }

  public void clear() {
    pause();
    gameSpeed = 1.0;
    game.clearGrid();
    updateGrid();
  }

  public void reset() {
    gameLoop.stop();
    generations = 0;
    updateGrid();
  }

  public double getGameSpeed() {
    return gameSpeed;
  }

  // TODO: replace gameSpeed variable with getRate/setRate?
  public void setGameSpeed(double gameSpeed) {
    this.gameSpeed = gameSpeed;
    if (gameLoop != null) {
      gameLoop.setRate(this.gameSpeed);
    }
  }

  public void findGameSpeed() {
    setGameSpeed(screenManager.getSliderValue());
    gameLoop.play();
  }

  public int getGenerations() {
    return generations;
  }

  public void gameScreenShow(Runnable onSave, Runnable onLoad, Runnable createNewGameAction, Runnable loadForResults) {
    screenManager.gameScreen(onSave, onLoad, this::pause, this::play, loadForResults,
        this::findGameSpeed, createNewGameAction, this::clear, generations, gameSpeed);
  }
}
