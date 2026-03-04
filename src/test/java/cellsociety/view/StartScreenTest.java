package cellsociety.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

/**
 * End-to-end TestFX tests for the application start screen.
 *
 * <p>Each test verifies the presence and behavior of the Language, Theme, and
 * Choice buttons, as well as the title label shown before a simulation is
 * loaded.</p>
 */
public class StartScreenTest extends DukeApplicationTest {

    // "Choose Game" is the English text for key "Choice" in Buttons_en.properties
    private static final String CHOICE_BUTTON_TEXT   = "Choose Game";
    private static final String LANGUAGE_BUTTON_TEXT = "Language";
    private static final String THEME_BUTTON_TEXT    = "Theme";
    private static final String TITLE_LABEL_TEXT     = "Cell Society";

    private boolean startTriggered;
    private Button choiceButton;
    private Button languageButton;
    private Button themeButton;
    private Labeled titleLabel;

    /**
     * Creates the StartScreen and stores references to its main components.
     * Runs automatically before each test.
     */
    @Override
    public void start(Stage stage) {
        startTriggered = false;
        new StartScreen(stage, () -> startTriggered = true);

        // components found by their visible text, reused across tests
        choiceButton   = lookup(CHOICE_BUTTON_TEXT).query();
        languageButton = lookup(LANGUAGE_BUTTON_TEXT).query();
        themeButton    = lookup(THEME_BUTTON_TEXT).query();
        titleLabel     = lookup(TITLE_LABEL_TEXT).query();
    }

    // ── layout ─────────────────────────────────────────────────────────────

    @Test
    void startScreen_titleLabel_showsCellSociety() {
        // GIVEN, app first starts up
        // WHEN, no action is taken
        // THEN, title label shows "Cell Society"
        assertLabelText(titleLabel, TITLE_LABEL_TEXT);
    }

    @Test
    void startScreen_languageButton_isVisible() {
        // GIVEN, app first starts up
        // WHEN, no action is taken
        // THEN, language button is present and visible
        assertNotNull(languageButton);
        assertTrue(languageButton.isVisible());
    }

    @Test
    void startScreen_themeButton_isVisible() {
        // GIVEN, app first starts up
        // WHEN, no action is taken
        // THEN, theme button is present and visible
        assertNotNull(themeButton);
        assertTrue(themeButton.isVisible());
    }

    @Test
    void startScreen_choiceButton_isVisible() {
        // GIVEN, app first starts up
        // WHEN, no action is taken
        // THEN, choice button is present and visible
        assertNotNull(choiceButton);
        assertTrue(choiceButton.isVisible());
    }

    // ── behavior ───────────────────────────────────────────────────────────

    @Test
    void choiceButton_click_triggersStartAction() {
        // GIVEN, start action has not yet been triggered
        // WHEN, choice button is clicked
        clickOn(choiceButton);
        // THEN, the provided start Runnable is invoked
        assertTrue(startTriggered, "Clicking Choice should invoke the start Runnable");
    }

    @Test
    void choiceButton_text_isChooseGame() {
        // GIVEN, app first starts up with English locale
        // WHEN, no action is taken
        // THEN, button displays its English label
        assertLabelText(choiceButton, CHOICE_BUTTON_TEXT);
    }

    @Test
    void languageButton_text_isLanguage() {
        // GIVEN, app first starts up with English locale
        // WHEN, no action is taken
        // THEN, button displays its English label
        assertLabelText(languageButton, LANGUAGE_BUTTON_TEXT);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private void assertLabelText(Labeled node, String expected) {
        assertEquals(expected, node.getText());
    }
}
