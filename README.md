# cell society
## TEAM NUMBER: 1
## NAMES: Joseph Wang, Georgiy Zemlevskiy, Michael Li

This project implements a cellular automata simulator supporting multiple simulation
types, grid topologies, neighborhoods, edge policies, and visual themes.

### Timeline

 * Start Date: January 22, 2026

 * Finish Date: March 3, 2026

 * Hours Spent: ~100 (combined across all team members)



### Attributions

 * Resources used for learning (including AI assistance)
   * Langton's Loop transition table: Christopher Langton (1984), "Self-reproduction in cellular automata"
   * SDSR Loop rules: Hiroki Sayama (1998), "Introduction of Structural Dissolution into Langton's Self-Reproducing Loop"
   * SugarScape model: Epstein & Axtell (1996), "Growing Artificial Societies"
   * Wa-Tor model: A.K. Dewdney (1984), Scientific American
   * JavaFX documentation: https://openjfx.io/javadoc/21/
   * AI assistance (Claude): Java/JavaFX capabilities, code review, debugging

 * Resources used directly (including AI assistance)
   * AI assistance (Claude): generating package-info.java documentation, debugging XML parameter mismatches


### Running the Program

 * Main class: `cellsociety.Main`

 * Data files needed:
   * Any `.xml` file from the `data/` directory.
   * Each file specifies simulation type, grid dimensions, initial state, parameters, and colors.
   * The `data/Error/` folder contains intentionally malformed files used only for tests — do not load these in the UI.

 * Interesting data files:
   * `data/LangtonsLoop/LangtonsLoopStandard.xml` — watch a loop self-reproduce over hundreds of steps
   * `data/LangtonsLoop/SDSRLoopStandard.xml` — dead loops dissolve, freeing space for new ones
   * `data/Topology/HexagonalShape2.xml` — ForestFire rendered with hexagonal cells and hexagonal neighborhood
   * `data/Topology/TriangularShape1.xml` — Game of Life rendered with alternating triangular cells
   * `data/SugarScape/SugarScapePreset1.xml` — agents migrate toward sugar peaks; emergent clustering visible in ~30 steps
   * `data/ForestFire/ForestFireDeterministic1.xml` — deterministic fire spread stopped by a firebreak (ignitionProbability = 0)
   * `data/Topology/ToroidalEdge1.xml` — glider wraps around the grid boundary indefinitely

 * Key/Mouse inputs:
   * **Click a cell** — toggles the cell's state by cycling to the next state (e.g. dead → alive → dead)
   * **Play/Pause button** — starts or pauses the animation
   * **Forward / Back buttons** — step one generation forward or backward (up to 20 steps back are stored)
   * **Speed slider** — adjusts simulation speed; drag left to slow down, right to speed up
   * **Reset button** — restores the grid to its original loaded state
   * **Clear button** — sets all cells to state 0
   * **Save button** — writes the current simulation state to an XML file via a file chooser dialog
   * **Load button** — opens a file chooser to load a new XML simulation into the current window
   * **New Game button** — opens a second independent simulation window
   * **Outline toggle** — shows or hides cell border lines on the grid
   * **Info button** — displays simulation metadata (title, author, description, parameters, colors)
   * **Edit Info button** — opens an editable form to change title, author, description, parameters, and colors; changes take effect on Save
   * **Language button (start screen)** — switches the UI language (English, Russian, Chinese)
   * **Theme button (start screen)** — switches the visual theme (currently Light)



### Notes/Assumptions

 * Assumptions or Simplifications:
   * New windows (simultaneous simulations) are placed in the center of the screen and are fully
     independent of other simulations. The program exits when the last remaining window is closed.
   * `stateDistribution` by `proportion` allocates cells deterministically in proportion order
     (i.e. the distribution is exact for exact multiples; remainder cells go to the last state).
   * WaTor fish and shark reproduction resets age only for the offspring; the parent's age continues accumulating.
   * SugarScape agents are placed randomly over the initial grid; the XML grid defines sugar levels (0–4), not agent positions.
   * Langton's Loop and SDSR Loop require the standard transition-table file (`LangtonsLoop.properties`) to be present on the classpath.
   * Grid history (step-back) stores at most 20 previous states. Stepping back past 20 steps is not supported.
   * FallingSand uses sequential (non-simultaneous) cell updates so that grains fall in a single step rather than requiring multiple steps to settle.

 * Known Bugs:
   * The file chooser's initial directory defaults to the user's home folder on some systems because the relative `data/` path resolution depends on the working directory set by the JVM launcher.
   * InfoView color pickers are initialised from the XML color strings; colors not representable as JavaFX `Color.web()` strings will silently revert to the default.

 * Features implemented:
   * **9 simulations**: Game of Life (custom rulestring), Forest Fire, Segregation, Percolation, Wa-Tor, Falling Sand, SugarScape, Langton's Loop, SDSR Loop
   * **3 neighborhood types**: Moore, Von Neumann, Extended Moore, Hexagonal, Triangular (5 total)
   * **3 edge policies**: Bounded (finite), Toroidal (wrapping), Mirror (clamped reflection)
   * **3 grid shapes**: Rectangle, Hexagon, Triangle
   * **Random initialisation**: `stateDistribution` by proportion or by exact count
   * **XML save/load**: full round-trip serialisation of simulation state
   * **Step forward and step backward** (20-step history)
   * **Variable speed** via slider
   * **Multi-window** simultaneous independent simulations
   * **Live cell editing** by clicking cells
   * **Info view and editable metadata** (title, author, description, parameters, colors)
   * **Localisation**: English, Russian, Chinese UI text
   * **CSS theming** via `StylableView` (Light theme; theme propagates to all open windows)
   * **Reflection-based factory**: new simulations, neighborhoods, and edge policies require only a new class and XML entry — no factory changes needed

 * Features unimplemented:
   * Dark mode / additional themes (CSS hooks exist but only Light theme is styled)
   * Saving user-edited colors back to XML (color edits apply to the running simulation but are not persisted unless Save is used)
   * Exporting a screenshot or animation of the grid

 * Noteworthy Features:
   * **Two-phase commit**: all simulations compute next states before committing any, guaranteeing correct simultaneous updates with no read-after-write errors
   * **Reflection-based ModelFactory**: game type, neighborhood, and edge policy are all instantiated by class name from the XML — adding a new simulation requires only a new subclass and XML file
   * **Immutable ParseResult**: all collection getters return unmodifiable views; `getCells()` returns a defensive deep copy, preventing accidental corruption across the model/controller boundary
   * **Grid aliasing fix**: `resetToInitialState()` copies the saved grid rather than aliasing it, so the initial snapshot is never overwritten by simulation steps
   * **SDSRLoop dissolution**: state 8 (DISSOLVE) causes dead loops to disintegrate, freeing space — behavior not present in the base Langton's Loop


### Assignment Impressions

The most challenging aspect is handling the data and ensuring proper encapsulation and separation between what the Model and View should know. The need to call certain methods return the necessary data and to pass Runnables to ensure the the data is returned to where it can all be compiled was an issue that often cropped up. The best way to handle this issue we found was to keep a physical diagram of the relationships between classes. 

Furthermore, it was hard managing the interaction between the reflection-based
factory, the XML parameter naming conventions, and the individual game constructors.
A single mismatched parameter key (e.g. `probCatch` vs `ignitionProbability`) unknowingly produced a "constructor could not be invoked" error with no indication of the root cause.
Writing end-to-end XML load tests for every data file was the most effective way to surface these issues systematically.

We thought that the MVC separation worked well for parallel development, since the model layer had no JavaFX dependencies at all, which made unit testing straightforward without needing TestFX or a display environment.
