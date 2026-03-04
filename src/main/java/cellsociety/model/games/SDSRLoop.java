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

  private static final int BACKGROUND = 0;
  private static final int CORE = 1;
  private static final int SHEATH = 2;
  private static final int CAP = 3;
  private static final int TURN = 4;
  private static final int DISCONNECT = 5;
  private static final int GUIDE = 6;
  private static final int STRAIGHT = 7;
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

    if (current == BACKGROUND) {
      return (inTube && contains(trbl, CORE)) ? CORE : BACKGROUND;
    }
    if (current == CORE && inTube) {
      return resolveSignalInTube(trbl);
    }
    if ((current == TURN || current == GUIDE || current == STRAIGHT) && inTube && contains(trbl, BACKGROUND)) {
      return BACKGROUND;
    }
    if (current == SHEATH) {
      if (contains(trbl, CAP)) {
        return CORE;
      }
      if (contains(trbl, SHEATH)) {
        return SHEATH;
      }
    }
    if (current == DISSOLVE) {
      return BACKGROUND;
    }

    return (current == BACKGROUND) ? BACKGROUND : DISSOLVE;
  }

  private int resolveWithDissolution(int current, int[] trbl) {
    if (current == BACKGROUND || current == CORE) {
      if (hasHigherState(trbl)) {
        return DISSOLVE;
      }
      return current;
    }
    if (current == SHEATH || current == CAP || current == DISCONNECT) {
      return BACKGROUND;
    }
    if (current == TURN || current == GUIDE || current == STRAIGHT) {
      return CORE;
    }
    if (current == DISSOLVE) {
      return BACKGROUND;
    }
    return DISSOLVE;
  }

  private int resolveSignalInTube(int[] trbl) {
    if (contains(trbl, STRAIGHT)) {
      return STRAIGHT;
    }
    if (contains(trbl, GUIDE)) {
      return GUIDE;
    }
    if (contains(trbl, TURN)) {
      return TURN;
    }
    return CORE;
  }

  private boolean isInTube(int[] trbl) {
    int count = 0;
    for (int s : trbl) {
      if (s == CORE || s == SHEATH || s == TURN || s == GUIDE || s == STRAIGHT) {
        count++;
      }
    }
    return count >= TUBE_THRESHOLD;
  }

  private boolean hasHigherState(int[] trbl) {
    for (int s : trbl) {
      if (s >= SHEATH && s <= STRAIGHT) {
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
