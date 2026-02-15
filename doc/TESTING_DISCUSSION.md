# Testing

### Georgiy Zemlevskiy, Joseph Wang, Michael Li

## Scenarios

- Pause button
  - Scenario: Pausing a running simulation. 
  - Given that the game is actually running a simulation,
  - When the pause button is clicked.
  - Then, the game is paused.

  - Play Button
    - Scenario: Starting a paused simulation
    - Given that the Timeline exists and is paused
    - When the play button is clicked
    - Then the game plays

  - Change Game Speed.
    - Scenario: Changing game speed while simulation is playing/paused.
    - Given that Timeline exists and is playing
    - When slider bar is moved(by arrows or mouse)
    - Then the game is stopped
      - TimeLine’s Keyframe is changed
      - Timeline is still paused

  - Reset Grid
    - Scenario: Resetting the grid to state specified in config file
    - Given: The simulation is running or paused.
    - When the reset button is pressed,
      - Then the grid is reset to the state specified in the config file. The simulation is not started.
  
  - Update cell state
    - Scenario: Changing a cell’s state within a grid will appropriately change from previous state to current.
    - Given: We are in the grid bounds and there is a valid cell state.
    - When: Whenever the simulation is running.

  - Clear Grid
    - Scenario: Clearing the grid.
    - Given: The simulation is running or paused.
    - When the clear button is pressed,
    - Then the every cell in the grid is reset to the respective game’s ‘default’ state, e.g. the ‘dead’ state in GoL.
