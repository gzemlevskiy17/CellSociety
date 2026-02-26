# Cell Society API Lab Discussion

### NAMES: Michael Li, Georgiy Zemlevskiy, Joseph Wang

### TEAM 1

## Current Configuration API

- Identified Public Classes/Methods
  public double getADoubleParam(String param) throws NumberFormatException: is redundant and should be removed.
  All other methods should exist. Most of the public methods either just create objects or are getters.

## Wish Configuration API

Now that you have a sense of the kind of service your project actually provides to programmers, think about the kind of service you wish it provided. In other words, spend this time designing the API instead of just having it happen. Write a simplified (i.e., less than "one page" each) description and goals for the Configuration API. Note which classes are abstract/interface and which are concrete (and justify why an abstraction is not appropriate).

- Goals
  Our current Configuration API works exactly as we wished.
  We should add an abstraction that will enable the parsing of config files of different formats.

- Contract
  - Services: The opportunity to use any format for the configuration file.

  - Abstractions and their Methods: We can construct an interface about XMLReader, which XMLReader and any classes created by the user to parse their own configuration file format. The interface would require corresponding classes to call parse.

public interface ConfigurationParser {
ParseResult parse(File file) throws ConfigurationException;
}

This method returns a ParseResult, which contains all of the parsed information of the configuration file, which ModelFactory and ViewFactory can then use.

- Exceptions: The Configuration API is run by another class GameRunner that handles the data flow and will pop up an ErrorView class to show the exception thrown.

## API Task Description

- English

Simulation gets its starting values from a data file
View selects a new style to apply to the running simulation

- Java

You insert a file, and the configuration system dispenses a ready-to-use Game object. The client doesn't need to know anything about XML parsing or how parameters are stored internally. You simply hand the file to XMLReader, receive a ParseResult and pass that ticket to ModelFactory which outputs the correct game.

The client loads a new configuration file, uses ViewFactory to build a new GridView with updated colors, edge policy, and grid outline, then hands it to ScreenManager to display.
