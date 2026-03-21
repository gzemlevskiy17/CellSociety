# Cell Society Design Final
### Team 1
### Michael Li, Joseph Wang, Georgiy Zemlevskiy

## Team Roles and Responsibilities

 * Michael Li: The role I had on the team was primarily backend development. I focused on making the game logic for almost every game and the XML reading and parsing logic and also helped out at the end with various shape neighborhood types.

 * Joseph Wang. Responsibilities include front-end implementation and ensuring all visual elements were implemented with data to support dynamic updates.

 * Georgiy Zemlevskiy: I worked on the controller, implemented 2 game types, some of the simulation API, and pivoted more to the front-end near the end of the project, particularly the implementation of languages and themes.

## Design Goals

 * Goal #1: Generalize the simulations as much as possible so that adding new simulations and features would be easy. Generalize the view and controller implementations so that adding new views and spawning new simulations would be easy.
 
 * Goal #2: Ensure model-view separation and ensure that all structures that store data are not exposed and instead internalized.
 
 * Goal #3: Implement multiple simultaneous simulations that are completely independent from each other in a clear and understandable way. The only elements common across all simulations are themes and languages, which are dynamically updated.
 
 * Goal #4: Build flexibility and robustness in simulation control, thus allowing for user control over front-end styling and back-end value storage.


#### How were Specific Features Made Easy to Add

 * Feature #1: Game is closed for modification (the step/commit loop never changes) but open for extension. This means any new simulation exists as a subclass of Game and overrides updateGame(), so adding a new simulation only requires a new subclass of Game and the custom updateGame() method. Once these are written, they will be instantiated with reflection by the ModelFactory when it encounters a configuration file with the new game type. Other classes that interact with the game like Grid, Controller, or GameScreen also don’t need to be changed. For example, Controller holds a reference to Game (the abstract class), not ForestFire or GameOfLife directly. It calls game.step(), game.reset(), game.getCell(), which are all defined on the abstraction. 

 * Feature #2: Cell shapes, neighborhood strategies, and edge behaviors were made easy to add using the strategy pattern, where the game’s Grid contains edge policy and neighborhood strategy objects that are instantiated by a factory using reflection on a string from the configuration file. There are neighborhood strategy classes that implement the NeighborhoodStrategy interface of cellsociety.model.topology.neighborhood   by providing a method that returns the neighbors of a coordinate and edge policy classes that implement the EdgePolicy interface of cellsociety.model.topology.edgebehavior by providing a method that returns the new position of out-of-bounds cells based on the strategy. 

This way, the edge policy and neighborhood strategy classes handle their respective ways of getting cell neighbors and out-of-bounds positions, so the game and its Grid don’t have to have conditionals to do that. So, in order to add a new edge policy or neighborhood strategy, only new classes that implement these interfaces need to be added. ModelFactory will automatically instantiate them when it reads the respective tag from the configuration file. 

Cell shapes are instantiated in a similar way, except on the view side, where the shape classes like RectangularGridView implement the GridView interface, and they encapsulate the cell shape display logic so that other view classes don’t have to deal with it. 

* Feature #3: To add a new theme, just one new .css file needs to be added, and the theme name needs to be added to Themes.properties, which is read by the dropdown theme selector. Once that theme is selected, it will be applied to every class that extends StylableView. A similar process should be followed to add a new language. 

* Feature #4: Error views. Adding a new error view, like an XML validation error, only requires adding a new error XML file to data/Error/ and adding a check in XMLValidator. The ErrorView dialog displays any message passed to it — no UI changes needed.    


## High-level Design

#### Core Classes and Abstractions, their Responsibilities and Collaborators

 * Class #1: Game: Serves as the abstract base for all simulation logic. It holds a reference to the Grid and exposes a step() method that implements the template method pattern: saveState() → updateGame() → commitChanges(). Subclasses implement only updateGame(), which defines the simulation-specific rules by reading current cell states and setting next states. The two-phase commit (setNextState() then commitChanges()) ensures all cells update synchronously, so no cell sees a neighbor's new state while computing its own. Also provides shared utilities like countNeighborsWithState() and getCell() that all subclasses use. Collaborators: Grid, Cell, Controller.

 ![alt text](<images/Screenshot 2026-03-20 233541.png>)



 * Class #2: Grid: Serves as the encapsulation of the simulation grid. It stores the 2D array of Cell objects and manages the step-back history as an ArrayDeque of full-grid snapshots. Exposes getNeighbors(row, col) which delegates to the injected NeighborhoodStrategy and EdgePolicy. The callers never need to know which topology is active. Also exposes saveState() and revertState() for the step-back feature. The internal cell array is never exposed directly; all access goes through getCell(row, col). Collaborators: Cell, NeighborhoodStrategy, EdgePolicy, Game.

![alt text](<images/Screenshot 2026-03-20 233555.png>)
![alt text](<images/Screenshot 2026-03-20 233601.png>)

 * Class #3: GameRunner: Serves as the setup layer that wires the model, view, and controller together for a single simulation window. Reads a configuration file via XMLReader, passes the ParseResult to ModelFactory and ViewFactory to create the Game and GridView, then constructs the Controller and GameScreen. Owns the lifecycle of one complete simulation instance. Collaborators: XMLReader, ModelFactory, ViewFactory, Controller, GameScreen, GameManager.


 * Class #4: ParseResult: Serves as the immutable data handoff between the configuration API and the rest of the system. XMLReader produces a ParseResult and every downstream class (ModelFactory, Controller, GameScreen, InfoView) reads from it. Because it is read-only, no class can accidentally mutate parsed data, and there is one canonical place to find all simulation configuration: game type, grid dimensions, initial cell states, game-specific parameters, and state-to-color mappings.

![alt text](<images/Screenshot 2026-03-20 233616.png>)


 * Class #5: ModelFactory & ViewFactory: Serve as reflection-based instantiation factories for the model and view layers, respectively. ModelFactory.createGame(parseResult) reads the game type string from ParseResult and uses reflection to construct the correct Game subclass without any switch statements or hardcoded type lists. ViewFactory does the same for grid shapes, constructing the correct GridView subclass from the <shape> tag. This is what makes adding new simulations and cell shapes require zero changes to existing code. The factories discover new classes automatically as long as they follow the naming convention.      


 * Class #6:  XMLReader: Serves as the entry point for all simulation configuration. Reads and validates an XML file, delegates format checks to XMLValidator, parses the grid (handling both explicit <cells> and <stateDistribution> initialization), extracts topology settings, game-specific parameters, and state colors, and returns a fully populated ParseResult. It is the only class in the system that knows anything about the XML file format, so changing the configuration format only requires changing XMLReader.

 ![alt text](<images/Screenshot 2026-03-20 233628.png>)


 * Class #7:  LanguageManager/ThemeManager: Serve as singleton observers that broadcast language and theme changes to all open windows simultaneously. Any view class that needs to respond to language or theme changes registers a listener via registerLanguageListener() or registerThemeListener(). When the user switches language or theme, all registered listeners are notified automatically. This is what enables Goal #3: multiple independent simulation windows that still share language and theme state without being directly coupled to each other. To avoid code duplication, views that need to be styled with different languages and themes extend StylableView which contains utility methods like registerLanguageListener() and registerThemeListener().

 ![alt text](<images/Screenshot 2026-03-20 233634.png>)
 ![alt text](<images/Screenshot 2026-03-20 233641.png>)
 ![alt text](<images/Screenshot 2026-03-20 233648.png>)



## Assumptions or Simplifications

 * Decision #1: All game configuration files would be in in XML format. 
 
 * Decision #2: Most hard-coded values would be stored in .properties files that had a similar name to class where they are used. The files that are accessed by these classes must be present for the game to run.
 
 * Decision #3: We assumed that the size of the user's screen is at least the hard-coded window size as the game screen is initially sized for at least a certain resolution of 1200 by 800.
 
 * Decision #4: We assumed the grid size was somewhat symmetrical, due to the calculations done for the position of cells from the grid assuming that the drawn grid will neatly fill the given space of 800 by 500 on the screen

 * Decision #5: We assumed all language, properties, and game files followed formats and had all the variables that the classes expected.

 * Decision #6: All defaults, such as the default light theme held in light.css and English properties files, are included to prevent crashes.

* Decision #7: Assumed all game class names exactly match the provided name in the <type> tag in XML, required for reflection to work        

* Decision #8: The maximum number of steps backwards that the game can perform is set at 20 (in a constant), so that the data structure holding the history won’t take up too much space.

## Changes from the Original Plan

 * Change #1: Adding multiple simultaneous windows required introducing GameManager above the existing stack. Because GameRunner was already self-contained, this was       
additive, and no existing class changed. However, language and theme sharing across windows required the singleton observer pattern (LanguageManager,ThemeManager). The original design had no mechanism for shared state.
 
 * Change #2: Originally, the neighborhood strategy was hardcoded to a default neighborhood. Now, Grid contains objects that implement NeighborhoodStrategy and EdgePolicy via a strategy pattern. 
 
 * Change #3: Many classes were divided in order to keep class size consistent. For example, originally XMLParser was a single class with around 400-500 lines or so. We split that up first into XMLReader and XMLWriter (for ParseResult), and then eventually XMLReader into itself and XMLValidator. This was done to ensure the Single Responsibility principle.

* Change #4: The original design assumed one simulation window at a time. This assumption was embedded in GameManager holding a single stage reference. When multi-window
Support was needed, that field became dead code.



## How to Add New Features

#### Features Designed to be Easy to Add

 * Feature #1: Games
 
 * Feature #2: Cell shapes, neighborhood strategies, edge behaviors.

 * Feature #3: Languages & Themes

* Feature #4: Error types / views                                           

* See the similar section above for more details.
 
#### Features Not Yet Done

 * Feature #1: Dynamic changes to the simulation logic, like changing game parameters, were not completely implemented. The controller logic between the view and the model was not complete, but the view’s side of the feature was implemented. 
 
 * Feature #2: A stats chart showing how many cells of each type existed within a step of the simulation. Each step, Controller already iterates all cells to call gridView.updateCell(). Adding a stats pass at the same time would count occurrences of each state into a map. That map would be passed to a new StatsView panel embedded in GameScreen which uses a JavaFX BarChart or LineChart to plot state counts over generations. 
 
 * Feature #3: Further generalization to views so that there is less interdependence between them. The StylableView class could be expanded to include more features common between views.
  
 * Feature #5: Grid zoom and pan, where we scroll/zoom into a large grid. Wrapping that Pane in a ScrollPane would give good support. Zoom could be implemented by wrapping the grid pane in a scale transform and dedicated zoom slider.

 
 * Feature #6: Support different configuration file formats, e.g. JSON. Introducing an interface with a single ParseResult method parse(File) would allow XMLReader to become one implementation and a new JsonReader to become another. GameRunner would select the correct implementation based on file extension.

                                                                                                                                        


