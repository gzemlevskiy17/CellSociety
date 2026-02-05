# Refactoring Lab Discussion
#### NAMES: Michael Li, Georgiy Zemlevskiy, Joseph Wang
#### TEAM 01


## Design Principles
 * Open/Close: The Open/Close principle states that software should be extended, not modified.

 * Liskov Substution: If you were to replace an inherited class with its superclass, the code should not break



### Current Abstractions

#### Abstraction #1
 * Current embodiment of principles: We have an abstract `Game` class that is extended by thw GameOfLife and ForestFire game classes. 
 
 * Improved embodiment of principles: We could have an interface (Simulation) that the AbstractGame depends on. This way, the localized class should now implement this interface instead of inheriting from each other. This allows us to decouple our classes and develop them independently without overlap. 


#### Abstraction #2
 * Current embodiment of principles: We have abstract `Grid` class

 * Improved embodiment of principles: This could also have interface allowing for Liskov Substitution and utilize the idea that 


### New Abstractions

#### Abstraction #1
 * Description: We could add a abstract `Neighbors` class, that would allow us to define methods to find neighbors.

 * How it supports making it easier to implement new features: the number of directions for neighbors is different for each grid type that can be implemented separately.
 

#### Abstraction #2
 * Description: We could create an abstract `` class

 * How it supports making it easier to implement new features
