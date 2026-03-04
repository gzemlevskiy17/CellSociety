package cellsociety.controller;

import java.util.HashMap;
import java.util.Map;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.ParseResult;
import cellsociety.view.GameScreen;
import cellsociety.view.grids.GridView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Coordinates the model (Game), view (GridView), and UI controls (GameScreen).
 *
 * <p>
 * Purpose: Drives the simulation loop, provides controls for play/pause/step,
 * synchronizes model
 * state with the grid view, and exposes simple save/load hooks used by the UI.
 * This class
 * encapsulates playback state (speed, generations) and directly manipulates the
 * model and view.
 * </p>
 *
 * <p>
 * Assumptions: The provided Game and GridView are non-null and consistent in
 * dimensions. UI calls
 * are expected to come from the JavaFX application thread. Out-of-bounds calls
 * to grid methods are
 * not performed by this class.
 * </p>
 *
 * <p>
 * Dependencies: cellsociety.model.Game, cellsociety.view.GridView,
 * cellsociety.view.GameScreen, and
 * JavaFX Timeline APIs.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * Controller ctrl = new Controller(game, gameScreen, gridView, 1.0, () -> {
 * });
 * ctrl.play();
 * }</pre>
 *
 * <p>
 * Notes: Generation history and undo functionality depend on the underlying
 * Game/Grid
 * implementations.
 * </p>
 *
 * @author Georgiy Zemlevskiy
 * @author Joseph Wang
 */
public class Controller {

  private Game game;
  private GameScreen gameScreen;
  private GridView gridView;
  private Timeline gameLoop;

  private static final double FRAMES_PER_SECOND = 0.5;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private double gameSpeed;
  private int generations;
  private int cellMax;

  /**
   * Constructs a Controller that coordinates the model, view, and UI controls.
   *
   * @param game                the game model to control
   * @param gameScreen          the screen manager used to display UI elements
   * @param gridView            the view responsible for rendering the grid
   * @param infoResult          contains game information parsed from XML; things
   *                            that need to be
   *                            displayed in the view are extracted from it
   * @param createNewGameAction action invoked to create a new game
   */
  public Controller(Game game, GameScreen gameScreen, GridView gridView, ParseResult infoResult,
      Runnable createNewGameAction) {
    this.game = game;
    this.gameScreen = gameScreen;
    this.gridView = gridView;
    cellMax = infoResult.getColorMap().size();
    generations = 0;
    gridView.addMouseEvents(this);
    gameScreen.setGridView(gridView);
    try {
      this.gameSpeed = Double.parseDouble(infoResult.getParams().get("speed"));
    } catch (Exception e) {
      this.gameSpeed = 1.0;
    }
    gameLoop = new Timeline(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> loop()));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.pause();
  }

  // TODO: reevaluate scopes of methods

  /**
   * Advances the simulation by a single step, updates the view, increments the
   * generation counter,
   * and updates the screen manager with the new generation count.
   */
  public void loop() {
    game.step();
    updateGrid();
    generations++;
    gameScreen.setGenerations(generations);
  }

  /**
   * Toggles outline for cells
   */
  public void toggleOutline() {
    gridView.toggleGridOutline();
  }

  /**
   * Enables changing of grid cells
   */
  public void pushFromScreentoGrid(int yRows, int xCols) {
    Cell cell = game.getCell(yRows, xCols);
    cell.setState(((cell.getState() + 1) % cellMax));
    cell.setNextState(cell.getState());
    gridView.updateCell(yRows, xCols, ((cell.getState() + 1) % cellMax));
  }

  /**
   * Synchronizes the GridView with the current states from the Game model.
   * Iterates all rows and
   * columns and updates each cell's displayed state.
   */
  public void updateGrid() {
    int rows = gridView.getNumRows();
    int cols = gridView.getNumCols();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell cell = game.getCell(r, c);
        gridView.updateCell(r, c, cell.getState());
      }
    }
  }

  /**
   * Pauses the automatic game loop.
   */
  public void pause() {
    gameLoop.pause();
  }

  /**
   * Starts or resumes the automatic game loop.
   */
  public void play() {
    gameLoop.play();
  }

  /**
   * Resets the GridView to its initial state Clears the current simulation:
   * pauses playback, resets
   * speed to 1.0, resets generations to 0, resets the grid state, and updates the
   * view
   * accordingly.
   */
  public void clear() {
    pause();
    gameSpeed = 1.0;
    generations = 0;
    game.clearGrid();
    gameScreen.simPausedButtonChange();
    updateGrid();
  }

  /**
   * Pauses the game, resets the generation counter to zero, and updates the view.
   */
  public void reset() {
    pause();
    generations = 0;
    game.reset();
    gameScreen.simPausedButtonChange();
    updateGrid();
  }

  /**
   * Steps the simulation one generation backward if possible. If a previous
   * generation exists,
   * restores it in the model, updates the view, decrements the generation
   * counter, and notifies the
   * screen manager.
   */
  public void stepBack() {
    if (generations > 0) {
      pause();
      gameScreen.simPausedButtonChange();
      game.stepBack();
      updateGrid();
      generations--;
      gameScreen.setGenerations(generations);
    }
  }

  public void stepForward() {
    pause();
    gameScreen.simPausedButtonChange();
    loop();
  }

  /**
   * Returns the current playback speed for the automatic game loop.
   *
   * @return the game speed (playback rate)
   */
  public double getGameSpeed() {
    return gameSpeed;
  }

  /**
   * Sets the playback speed for the automatic game loop. If a timeline exists,
   * its rate is updated
   * immediately.
   *
   * @param gameSpeed the new playback rate to apply
   */
  public void setGameSpeed(double gameSpeed) {
    this.gameSpeed = gameSpeed;
    if (gameLoop != null) {
      gameLoop.setRate(this.gameSpeed);
    }
  }

  /**
   * Reads the slider value from the screen manager and applies it as the new game
   * speed, then
   * starts playback.
   */
  public void findGameSpeed() {
    setGameSpeed(gameScreen.getSliderValue());
  }

  /**
   * Returns the number of generations that have been simulated since the last
   * reset.
   *
   * @return the generation count
   */
  public int getGenerations() {
    return generations;
  } // TODO: decide if needed

  /**
   * Requests the screen manager to show the game screen and wires UI callbacks
   * for
   * save/load/play/pause/clear and other controls.
   *
   * @param onSave              action to run when the user saves
   * @param onLoad              action to run when the user requests load
   * @param createNewGameAction action to create a new game
   * @param loadForResults      action used to reload the current configuration
   *                            for results view
   */
  public void gameScreenShow(ParseResult parsingInfo, Runnable onSave, Runnable onLoad,
      Runnable createNewGameAction,
      Runnable loadForResults, Runnable pullChanges) {
    gameScreen.gameScreen(parsingInfo, onSave, onLoad, this::pause, this::play, loadForResults,
        this::findGameSpeed, createNewGameAction, this::clear, this::stepForward, this::stepBack, pullChanges,
        this::toggleOutline, generations,
        gameSpeed); // FIXME: don't pass parameters straight on
    setGameSpeed(gameSpeed);
    gameScreen.simPausedButtonChange();
  }

  public void toBeUpdates(ParseResult changes) {
    game.updateParams(changes.getParams());

    Map<Integer, Color> colorMap = new HashMap<>();
    Map<Integer, String> parsedColors = changes.getColorMap();
    for (Integer id : parsedColors.keySet()) {
      colorMap.put(id, Color.web(parsedColors.get(id)));
    }
    gridView.setAllColors(colorMap);
  }
}
