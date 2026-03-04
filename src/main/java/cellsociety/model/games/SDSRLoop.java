package cellsociety.model.games;

import cellsociety.model.Grid;
import java.util.Map;

/**
 * SDSR (Structurally Dissolvable Self-Reproducing) Loop by Hiroki Sayama (1998).
 *
 * <p>
 * Extends {@link LangtonsLoop} with a 9th state (state 8 - dissolution).
 * When a loop becomes surrounded and cannot reproduce, state 8 propagates
 * through it, dissolving the structure back to background (state 0). This
 * enables continuous growth and turn-over of generations, producing a
 * dynamic colony rather than a static dead core.
 * </p>
 *
 * <p>
 * Additional state:
 * <ul>
 *   <li>8 - Dissolution (destroys loop structure)</li>
 * </ul>
 * </p>
 *
 * @author Michael Li
 * @see LangtonsLoop
 */
public class SDSRLoop extends LangtonsLoop {

  private static final int DISSOLVE = 8;
  private static final int TUBE_THRESHOLD = 2;

  /**
   * Constructs an SDSR Loop simulation.
   *
   * @param grid   the grid with initial cell states (0-8)
   * @param params simulation parameters
   */
  public SDSRLoop(Grid grid, Map<String, String> params) {
    super(grid, params);
    addRule(1, 1, 1, 5, 2, DISSOLVE);
    addRule(1, 2, 1, 1, 5, DISSOLVE);
    addRule(1, 5, 2, 1, 1, DISSOLVE);
    addRule(1, 1, 5, 2, 1, DISSOLVE);
  }

  /**
   * Handles configurations not found in the base Langton's transition table.
   * Implements Sayama's dissolution rules for state 8 propagation.
   */
  @Override
  protected int resolveUndefined(int current, int north, int east, int south, int west) {
    int[] trbl = {north, east, south, west};
    boolean hasDissolve = contains(trbl, DISSOLVE);

    if (hasDissolve) {
      return resolveWithDissolution(current, trbl);
    }

    boolean inTube = isInTube(trbl);

    if (current == 0) {
      return (inTube && contains(trbl, 1)) ? 1 : 0;
    }
    if (current == 1 && inTube) {
      return resolveSignalInTube(trbl);
    }
    if ((current == 4 || current == 6 || current == 7) && inTube && contains(trbl, 0)) {
      return 0;
    }
    if (current == 2) {
      if (contains(trbl, 3)) {
        return 1;
      }
      if (contains(trbl, 2)) {
        return 2;
      }
    }
    if (current == DISSOLVE) {
      return 0;
    }

    return (current == 0) ? 0 : DISSOLVE;
  }

  private int resolveWithDissolution(int current, int[] trbl) {
    if (current == 0 || current == 1) {
      if (hasHigherState(trbl)) {
        return DISSOLVE;
      }
      return current;
    }
    if (current == 2 || current == 3 || current == 5) {
      return 0;
    }
    if (current == 4 || current == 6 || current == 7) {
      return 1;
    }
    if (current == DISSOLVE) {
      return 0;
    }
    return DISSOLVE;
  }

  private int resolveSignalInTube(int[] trbl) {
    if (contains(trbl, 7)) {
      return 7;
    }
    if (contains(trbl, 6)) {
      return 6;
    }
    if (contains(trbl, 4)) {
      return 4;
    }
    return 1;
  }

  private boolean isInTube(int[] trbl) {
    int count = 0;
    for (int s : trbl) {
      if (s == 1 || s == 2 || s == 4 || s == 6 || s == 7) {
        count++;
      }
    }
    return count >= TUBE_THRESHOLD;
  }

  private boolean hasHigherState(int[] trbl) {
    for (int s : trbl) {
      if (s >= 2 && s <= 7) {
        return true;
      }
    }
    return false;
  }

  private boolean contains(int[] arr, int value) {
    for (int s : arr) {
      if (s == value) {
        return true;
      }
    }
    return false;
  }
}
