package cellsociety.model.games;

import cellsociety.model.Cell;
import cellsociety.model.Game;
import cellsociety.model.Grid;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * General Game of Life simulation supporting rulestring variations.
 *
 * <p>
 * Supports two rulestring formats:
 * </p>
 * <ul>
 * <li>B/S notation: "B3/S23" (birth digits / survival digits)</li>
 * <li>S/B notation: "23/3" (survival digits / birth digits)</li>
 * </ul>
 *
 * <p>
 * Defaults to classic Conway rules (B3/S23) if no rulestring is provided.
 * </p>
 *
 * @author Michael Li
 * @see Game
 */
public class GameOfLife extends Game {

    public static final int DEAD = 0;
    public static final int ALIVE = 1;

    private static final Pattern BS_PATTERN = Pattern.compile("B(\\d*)/S(\\d*)");
    private static final Pattern SB_PATTERN = Pattern.compile("(\\d*)/(\\d*)");
    private static final Logger LOG = LogManager.getLogger(GameOfLife.class);

    private final Set<Integer> birthCounts;
    private final Set<Integer> survivalCounts;
    private static final String[] newVariables = new String[] { "rulestring" };

    public GameOfLife(Grid grid) {
        this(grid, "");
    }

    public GameOfLife(Grid grid, String rulestring) {
        super(grid);
        RuleCounts counts = parseRulestring(rulestring);
        birthCounts = counts.birth();
        survivalCounts = counts.survival();
    }

    public GameOfLife(Grid grid, Map<String, String> params) {
        super(grid, params);
        RuleCounts counts = parseRulestring(getParam(newVariables[0]));
        birthCounts = counts.birth();
        survivalCounts = counts.survival();
    }

    private static RuleCounts parseRulestring(String rulestring) {
        if (rulestring == null || rulestring.isEmpty()) {
            return new RuleCounts(digitsToSet("3"), digitsToSet("23"));
        }
        Matcher bsMatcher = BS_PATTERN.matcher(rulestring);
        Matcher sbMatcher = SB_PATTERN.matcher(rulestring);
        if (bsMatcher.matches()) {
            return new RuleCounts(digitsToSet(bsMatcher.group(1)), digitsToSet(bsMatcher.group(2)));
        } else if (sbMatcher.matches()) {
            return new RuleCounts(digitsToSet(sbMatcher.group(2)), digitsToSet(sbMatcher.group(1)));
        } else {
            String message = "Invalid rulestring: " + rulestring;
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    protected void updateGame() {
        for (int i = 0; i < getGrid().getRows(); i++) {
            for (int j = 0; j < getGrid().getColumns(); j++) {
                Cell cell = getGrid().getCell(i, j);
                int aliveNeighbors = countNeighborsWithState(getGrid().getNeighbors(i, j), ALIVE);

                if (cell.getState() == ALIVE) {
                    cell.setNextState(survivalCounts.contains(aliveNeighbors) ? ALIVE : DEAD);
                } else {
                    cell.setNextState(birthCounts.contains(aliveNeighbors) ? ALIVE : DEAD);
                }
            }
        }
    }

    private static Set<Integer> digitsToSet(String digits) {
        Set<Integer> set = new HashSet<>();
        for (char c : digits.toCharArray()) {
            set.add(Character.getNumericValue(c));
        }
        return set;
    }

    private record RuleCounts(Set<Integer> birth, Set<Integer> survival) {
    }
}
