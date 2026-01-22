# Breakout Abstractions Lab Discussion
#### Michael Li, Joseph Wang, Georgiy Zemlevskiy

We used Georgiy's repository to perform our analysis. 

### Block

This superclass's purpose as an abstraction:
```java
 public class Block {
     public int something ()
     // no implementation, just a comment about its purpose in the abstraction 
 }
```

#### Subclasses

Each subclass's high-level behavorial differences from the superclass

We could have exploding blocks, power-up blocks, indestructible, and all the different variants made into subclasses of the abstract Block class. All of these classes will have a getDamage() function that will return how much damage is dealt so the score is incremented.  


#### Affect on Game/Level class

The main class doesn't particularly benefit from an abstraction of the explosion method, but a seperate interface for the Explosion Block class could be useful in just handling explosive collisions specifically. 

### Power-up

This superclass's purpose as an abstraction:
```java
 public class PowerUp {
     public int something ()
     // no implementation, just a comment about its purpose in the abstraction 
 }
 ```

Furthermore, for applying the powerups, we don't need to necessarily check each type of powerup at once, but rather handle that power up indiviudally via its own power up type, e.g making a Spawn class (coupled with dropPowerUp() when block is destroyed). 


#### Subclasses

Extra balls subclass, FastBall subclass, are some subclasses that could be used.

#### Affect on Game/Level class

We would not have a powerUpStatus array in the main class, and would instead utilize these classes and their respective methods.

### Others?