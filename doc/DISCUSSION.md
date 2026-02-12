# Testing
### Georgiy Zemlevskiy, Joseph Wang, Michael Li

Start by going over your existing tests together to verify they are giving you confidence that your code works correctly, rather than just meeting "minimum quotas" to have tests. Resist the urge to simply create a lot of "random" tests (i.e., the shotgun approach) and focus on making each test as useful as possible (i.e., the sniper approach).

Discuss how to test your project more effectively, such as:

making smaller classes and methods that can be tested more easily
reducing class and method dependencies so inputs can be provided more flexibly or directly
creating return values that can be tested rather than using getters to check internal state changes made by void methods
brainstorming different bad data file values, and how to test and handle them

Example: 

Feature: User trades stocks
  Scenario: User requests a sell before close of trading
    Given I have 100 shares of MSFT stock
       And I have 150 shares of APPL stock
       And the time is before close of trading


## Scenarios

### 

*Feature: Pause button
    Scenario: Simulation of game is running and pause button is pressed
        Given that the game is actually running a simulation, when the pause button is clicked, the game is paused.
