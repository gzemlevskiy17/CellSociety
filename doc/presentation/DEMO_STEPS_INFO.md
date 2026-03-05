This information was for reference purpose to the project demonstrators only and were not displayed
during the demonstration.
The slideshow '308 Presentation.pdf', however, was displayed during the presentation.

### Functionality demonstration steps

1. Demonstrate basic functionality:
2. Select the Solarized theme {goal: dynamic theme setting; design: observer pattern that will be
   covered later}.
3. Choose GameOfLifeGlider.xml, but cancel the filechooser first and actually load it the second
   time to demonstrate that lack of choice does no harm.
4. Play/Pause, Step Forward, Step Back (shows history) {goal: MVC separation; design: the controller
   synchronizes the model and view by calling methods from both. Model and view never call each
   other.}
5. Click a cell to toggle its state manually. Make a cell green so that it will disappear in the
   next generation.
6. Toggle grid outline on/off
7. Display information
8. Set the speed to be slow so that the simulation runs in the background. {goal: MVC separation;
   design: define UI behaviors in controller classes and apply them in view classes by passing
   functionality}
9. Click the new game button {goal: completely independent states except for language and theme;
   design: LanguageManager and ThemeManager are singletons}
10. Show ExtendedMooreNeighborhood2.xml in the ide, show the neighborhood parameter.
11. Choose ExtendedMooreNeighborhood2.xml
12. Let it run for just 1 generation. Point out the extended neighborhood.
13. Change the speed and compare it to the window in the background, point out that they are
    independent.
14. Pause at any generation in the future.
15. Press the save button. Save the simulation as savetest.xml in the data folder.
16. Clear the screen
17. Load savetest.xml to demonstrate saving & loading.
18. Close the first window with game of life. {demonstrates independence of simulations}
19. Clear the screen
20. Briefly demonstrate edge effects:
21. Show ToroidalEdge1.xml in the ide. Explain where the edgepolicy is set.
22. Load ToroidalEdge1.xml
23. Increase the speed a little so that it doesn’t take as long.
24. Run until toroidal is observed.
25. Clear the screen
26. Demonstrate bad data:
27. Show OutOfBounds.xml in the ide - explain where the error should come from.
28. Choose OutOfBounds.xml
29. Observe errorview dialog
30. IF WE HAVE TIME:
31. NEW GAME
32. Choose the Dark theme.
33. Choose the Russian language.
34. Show the WatorFishDominant.xml in the ide.
35. Choose the WatorFishDominant.xml
36. Observe fish domination.

### Tests and Information

- ForestFireTest.Step_TreeIgnitionProbability1_Burns() - gets rid of randomness by providing 1 to
  treeIgnition parameter.

- GameOfLifeTest.testHighLifeBirthAtSix() - rulestring test
- GameOfLifeTest.testInvalidRuleStringThrows()
- SugarScapeTest.multipleStepsReducePopulationOnBarrenGrid()
  What it sets up
  Grid: int[][] states = new int[5][5];
  That’s a 5×5 grid full of zeros. In your SugarScape encoding, 0 is “no sugar” (barren).
  Agents: it sets numAgents to "10", so the simulation starts with 10 agents placed somewhere.
  Makes agents weak + hungry:
  minInitSugar = 1, maxInitSugar = 2 → agents start with very little stored sugar.
  minMetabolism = 3, maxMetabolism = 4 → agents burn 3–4 sugar per step.
  So: very little initial energy, very high burn rate, and no sugar available to replenish.

What it checks
After 20 steps, it counts how many grid cells show state 5:

Why this test is interesting (and what it’s really guarding)
It’s guarding against bugs like
agents never dying when sugar hits 0/negative,
metabolism not being applied,
agents “creating” sugar out of nowhere on a barren map,
death happening internally but the grid display not reflecting it (still showing agents as 5).

- SugarScapeTest.testMetabolizeKillsAgentWhenSugarGoesNegative()

- GameScreenTest.forwardButton_clickThrice_showsGenerationThree()
- GameScreenTest.backButton_atGenerationZero_doesNotDecrementBelowZero()
- GameScreenTest.slider_setToZero_gameSpeedClampsToOne() - no 0 slider speed