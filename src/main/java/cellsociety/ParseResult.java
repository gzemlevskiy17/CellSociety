package cellsociety;

public class ParseResult {
    private final int rows;
    private final int cols;
    private final String gameType;
    private final int[][] cells; // may be null if not provided
    private final double probCatch;
    private final double probGrow;
    private final java.util.Map<Integer, String> colorMap;

    public ParseResult(int rows, int cols, String gameType, int[][] cells, double probCatch, double probGrow, java.util.Map<Integer, String> colorMap) {
        this.rows = rows;
        this.cols = cols;
        this.gameType = gameType;
        this.cells = cells;
        this.probCatch = probCatch;
        this.probGrow = probGrow;
        this.colorMap = colorMap;
    }

    public java.util.Map<Integer, String> getColorMap() {
        return colorMap;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String getGameType() {
        return gameType;
    }

    public int[][] getCells() {
        return cells;
    }

    public double getProbCatch() {
        return probCatch;
    }

    public double getProbGrow() {
        return probGrow;
    }
}
