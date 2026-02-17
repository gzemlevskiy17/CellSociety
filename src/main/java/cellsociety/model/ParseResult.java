package cellsociety.model;

import java.util.Map;

/**
 * Immutable data container holding all values parsed from a simulation XML
 * configuration file.
 *
 * <p>
 * Acts as the bridge between {@link cellsociety.model.XMLParser} and the rest
 * of the
 * application. The parser produces a {@code ParseResult}, which is then
 * consumed by
 * factories ({@link cellsociety.model.ModelFactory},
 * {@link cellsociety.view.ViewFactory})
 * and the controller to build the simulation.
 * </p>
 *
 * <p>
 * <b>Assumptions:</b> {@code cells} may be null when the XML uses
 * {@code <stateDistribution>} or omits cell data entirely. Numeric parameters
 * default
 * to 0.0 if absent from the XML, except {@code speed} which defaults to 1.0.
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> None — this is a plain data class with no external
 * dependencies.
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * ParseResult result = XMLParser.parse(new File("data/GameOfLifeSimple.xml"));
 * int rows = result.getRows();
 * String type = result.getGameType();
 * int[][] cells = result.getCells(); // may be null
 * }</pre>
 *
 * @author Michael Li
 */
public class ParseResult {
    private final int rows;
    private final int cols;
    private final String gameType;
    private final int[][] cells;
    private final Map<String, Double> params;
    private final Map<Integer, String> colorMap;
    private final String author;
    private final String title;
    private final String description;

    /**
     * Constructs a ParseResult with all simulation configuration values.
     *
     * @param rows        number of grid rows
     * @param cols        number of grid columns
     * @param gameType    simulation type (e.g., "GameOfLife", "ForestFire",
     *                    "Segregation")
     * @param title       simulation title from the XML
     * @param author      simulation author from the XML
     * @param description simulation description from the XML
     * @param cells       2D array of initial cell states, or null if
     *                    algorithmically generated
     * @param probCatch   fire ignition probability (ForestFire)
     * @param probGrow    tree growth probability (ForestFire)
     * @param speed       simulation speed multiplier
     * @param threshold   satisfaction threshold (Segregation)
     * @param colorMap    mapping of state IDs to hex color strings
     */
    public ParseResult(int rows, int cols, String gameType, String title, String author, String description,
            int[][] cells, Map<String, Double> params,
            Map<Integer, String> colorMap) {
        this.rows = rows;
        this.cols = cols;
        this.gameType = gameType;
        this.title = title;
        this.author = author;
        this.description = description;
        this.cells = cells;
        this.params = params;
        this.colorMap = colorMap;
    }

    /** @return mapping of state IDs to hex color strings (e.g., 0 -> "#FFFFFF") */
    public Map<Integer, String> getColorMap() {
        return colorMap;
    }

    /** @return number of grid rows */
    public int getRows() {
        return rows;
    }

    /** @return number of grid columns */
    public int getCols() {
        return cols;
    }

    /** @return simulation type identifier (e.g., "GameOfLife") */
    public String getGameType() {
        return gameType;
    }

    /** @return simulation author name */
    public String getAuthor() {
        return author;
    }

    /** @return simulation description text */
    public String getDescription() {
        return description;
    }

    /** @return simulation title */
    public String getTitle() {
        return title;
    }

    /** @return 2D array of initial cell states, or null if not provided */
    public int[][] getCells() {
        return cells;
    }

    /** @return Map of parameters */
    public Map<String, Double> getParams() {
        return params;
    }

    /** @return parameter from Map of parameters */
    public double getAParam(String param) {
        if (params.containsKey(param)) {
            return params.get(param);
        }
        return 0.0;
    }

    /** @return fire ignition probability for ForestFire simulations */
    public double getProbCatch() {
        return getAParam("ignitionProbability");
    }

    /** @return tree growth probability for ForestFire simulations */
    public double getProbGrow() {
        return getAParam("fillProbability");
    }

    /** @return simulation speed multiplier */
    public double getSpeed() {
        return getAParam("speed");
    }

    /** @return satisfaction threshold for Segregation simulations */
    public double getThreshold() {
        return getAParam("threshold");
    }
}
