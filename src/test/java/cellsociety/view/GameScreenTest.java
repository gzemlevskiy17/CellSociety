package cellsociety.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellsociety.controller.Controller;
import cellsociety.model.Game;
import cellsociety.model.ModelFactory;
import cellsociety.model.ParseResult;
import cellsociety.model.XMLReader;
import cellsociety.view.grids.GridView;
import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

/**
 * End-to-end TestFX tests for the game simulation screen.
 *
 * <p>Each test starts from a freshly wired game screen (GameOfLife 3x3,
 * deterministic) and simulates user interactions via button clicks and slider
 * adjustments, then asserts observable outcomes in both the UI labels and the
 * underlying model state.</p>
 */
public class GameScreenTest extends DukeApplicationTest {

    private static final String SIMPLE_FILE = "data/GameOfLife/GameOfLifeSimple.xml";

    private Game game;
    private Controller controller;
    private ParseResult parseResult;

    private Button playButton;
    private Button forwardButton;
    private Button backButton;
    private Button clearButton;
    private Button resetButton;
    private Slider speedSlider;
    private Labeled generationLabel;

    /**
     * Wires the full model-view-controller stack without going through the
     * start-screen file chooser, then displays the game screen.
     * Runs automatically before each test.
     */
    @Override
    public void start(Stage stage) throws Exception {
        parseResult = XMLReader.parse(new File(SIMPLE_FILE));

        ModelFactory modelFactory = new ModelFactory();
        ViewFactory viewFactory = new ViewFactory();
        game = modelFactory.createGame(parseResult);
        GridView gridView = viewFactory.createView(parseResult);

        GameScreen gameScreen = new GameScreen(stage);
        controller = new Controller(game, gameScreen, gridView, parseResult, () -> {});

        controller.gameScreenShow(
            parseResult,
            () -> {},           // onSave   (no-op)
            () -> {},           // onLoad   (no-op)
            () -> {},           // newGame  (no-op)
            controller::reset,  // loadForResults → Reset button
            () -> {}            // pullChanges (no-op)
        );

        stage.show();

        // components found by their CSS IDs or visible text; reused across tests
        playButton      = lookup("Play").query();
        forwardButton   = lookup("Forward").query();
        backButton      = lookup("Back").query();
        clearButton     = lookup("Clear").query();
        resetButton     = lookup("Reset").query();
        speedSlider     = lookup(".slider").query();
        generationLabel = lookup("#generation-label").query();
    }

    // ── generation counter ─────────────────────────────────────────────────

    @Test
    void gameScreen_started_showsGenerationZero() {
        // GIVEN, app first starts up
        // WHEN, no action is taken
        // THEN, generation counter shows zero
        assertGenerationEquals(0);
    }

    @Test
    void forwardButton_click_incrementsGenerationByOne() {
        // GIVEN, app first starts up
        // WHEN, forward button is clicked once
        clickOn(forwardButton);
        // THEN, generation counter shows one
        assertGenerationEquals(1);
    }

    @Test
    void forwardButton_clickThrice_showsGenerationThree() {
        // GIVEN, app first starts up
        // WHEN, forward button is clicked three times
        clickOn(forwardButton);
        clickOn(forwardButton);
        clickOn(forwardButton);
        // THEN, generation counter shows three
        assertGenerationEquals(3);
    }

    @Test
    void backButton_atGenerationZero_doesNotDecrementBelowZero() {
        // GIVEN, app first starts up at generation zero
        // WHEN, back button is clicked
        clickOn(backButton);
        // THEN, counter stays at zero (no underflow)
        assertGenerationEquals(0);
    }

    @Test
    void forwardTwice_thenBackTwice_returnsToGenerationZero() {
        // GIVEN, app first starts up
        // WHEN, forward is clicked twice then back is clicked twice
        clickOn(forwardButton);
        clickOn(forwardButton);
        clickOn(backButton);
        clickOn(backButton);
        // THEN, counter returns to zero
        assertGenerationEquals(0);
    }

    @Test
    void backButton_afterOneForward_returnsToGenerationZero() {
        // GIVEN, simulation has advanced one generation
        clickOn(forwardButton);
        // WHEN, back button is clicked
        clickOn(backButton);
        // THEN, counter returns to zero
        assertGenerationEquals(0);
    }

    // ── play / pause button ────────────────────────────────────────────────

    @Test
    void playButton_initialText_isPlay() {
        // GIVEN, app first starts up (simulation is paused)
        // WHEN, no action is taken
        // THEN, play button shows "Play"
        assertEquals("Play", playButton.getText());
    }

    @Test
    void playButton_firstClick_textChangesToPause() {
        // GIVEN, simulation is paused and button shows "Play"
        // WHEN, play button is clicked once
        clickOn(playButton);
        // THEN, button text changes to "Pause" to indicate simulation is running
        assertEquals("Pause", playButton.getText());
    }

    @Test
    void playButton_secondClick_textChangesBackToPlay() {
        // GIVEN, simulation is running (play clicked once)
        clickOn(playButton);
        // WHEN, play button is clicked again to pause
        clickOn(playButton);
        // THEN, button text returns to "Play"
        assertEquals("Play", playButton.getText());
    }

    // ── clear ──────────────────────────────────────────────────────────────

    @Test
    void clearButton_click_allModelCellsAreZero() {
        // GIVEN, simulation has advanced so cells are non-zero
        clickOn(forwardButton);
        // WHEN, clear button is clicked
        clickOn(clearButton);
        // THEN, all model cells are set to zero (empty)
        for (int r = 0; r < parseResult.getRows(); r++) {
            for (int c = 0; c < parseResult.getCols(); c++) {
                assertEquals(0, game.getCell(r, c).getState(),
                    "Cell (" + r + "," + c + ") should be 0 after clear");
            }
        }
    }

    @Test
    void clearButton_click_resetsGenerationCounterToZero() {
        // GIVEN, simulation has advanced two generations
        clickOn(forwardButton);
        clickOn(forwardButton);
        // WHEN, clear button is clicked
        clickOn(clearButton);
        // THEN, generation counter resets to zero
        assertGenerationEquals(0);
    }

    // ── reset ──────────────────────────────────────────────────────────────

    @Test
    void resetButton_click_restoresInitialCellStates() {
        // GIVEN, the initial cell states are recorded and simulation has advanced
        int[][] initial = parseResult.getCells();
        clickOn(forwardButton);
        clickOn(forwardButton);
        clickOn(forwardButton);
        // WHEN, reset button is clicked
        clickOn(resetButton);
        // THEN, all cells match the original initial configuration
        for (int r = 0; r < parseResult.getRows(); r++) {
            for (int c = 0; c < parseResult.getCols(); c++) {
                assertEquals(initial[r][c], game.getCell(r, c).getState(),
                    "Cell (" + r + "," + c + ") should match initial state after reset");
            }
        }
    }

    @Test
    void resetButton_click_resetsGenerationCounterToZero() {
        // GIVEN, simulation has advanced one generation
        clickOn(forwardButton);
        // WHEN, reset button is clicked
        clickOn(resetButton);
        // THEN, generation counter resets to zero
        assertGenerationEquals(0);
    }

    // ── speed slider ───────────────────────────────────────────────────────

    @Test
    void slider_setToMax_gameSpeedEqualsMax() {
        // GIVEN, app first starts up
        // WHEN, slider is moved to its maximum value
        double max = speedSlider.getMax();
        setValue(speedSlider, max);
        // THEN, controller game speed matches the slider maximum
        assertEquals(max, controller.getGameSpeed(), 0.01);
    }

    @Test
    void slider_setToMid_gameSpeedEqualsMid() {
        // GIVEN, app first starts up
        // WHEN, slider is set to mid-point value 5
        setValue(speedSlider, 5.0);
        // THEN, controller game speed equals 5
        assertEquals(5.0, controller.getGameSpeed(), 0.01);
    }

    @Test
    void slider_setToZero_gameSpeedClampsToOne() {
        // GIVEN, app first starts up
        // WHEN, slider is set to zero
        setValue(speedSlider, 0.0);
        // THEN, game speed is clamped to 1 (getSliderValue returns 1 when value <= 0)
        assertEquals(1.0, controller.getGameSpeed(), 0.01);
    }

    // ── cell click (state cycling) ─────────────────────────────────────────

    @Test
    void cellClick_deadCell_cyclesStateToAlive() {
        // GIVEN, cell (0,0) starts as 0 (dead) in GameOfLifeSimple
        int initial = game.getCell(0, 0).getState();
        int colorCount = parseResult.getColorMap().size();
        // WHEN, cell is clicked via controller
        runAsJFXAction(() -> controller.pushFromScreentoGrid(0, 0));
        // THEN, state advances by one modulo number of states
        assertEquals((initial + 1) % colorCount, game.getCell(0, 0).getState());
    }

    @Test
    void cellClick_aliveCell_cyclesStateTowardsZero() {
        // GIVEN, cell (1,1) starts as 1 (alive) in GameOfLifeSimple
        int initial = game.getCell(1, 1).getState();
        int colorCount = parseResult.getColorMap().size();
        // WHEN, cell is clicked via controller
        runAsJFXAction(() -> controller.pushFromScreentoGrid(1, 1));
        // THEN, state advances by one modulo number of states
        assertEquals((initial + 1) % colorCount, game.getCell(1, 1).getState());
    }

    @Test
    void cellClick_twice_returnsToOriginalState() {
        // GIVEN, a 2-state simulation; cell (0,0) starts dead
        int initial = game.getCell(0, 0).getState();
        // WHEN, cell is clicked twice (full cycle)
        runAsJFXAction(() -> controller.pushFromScreentoGrid(0, 0));
        runAsJFXAction(() -> controller.pushFromScreentoGrid(0, 0));
        // THEN, state returns to original value
        assertEquals(initial, game.getCell(0, 0).getState());
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private void assertGenerationEquals(int expected) {
        assertEquals("Gen: " + expected, generationLabel.getText());
    }
}
