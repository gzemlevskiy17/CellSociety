package cellsociety;

public class ParseResult {
    private final int rows;
    private final int cols;
    private final String gameType;
    private final int[][] cells; // may be null if not provided
    private final double probCatch;
    private final double probGrow;
    private final java.util.Map<Integer, String> colorMap;
    private final String author;
    private final String title;
    private final String description;

    public ParseResult(int rows, int cols, String gameType, String title, String author, String description,
            int[][] cells, double probCatch, double probGrow,
            java.util.Map<Integer, String> colorMap) {
        this.rows = rows;
        this.cols = cols;
        this.gameType = gameType;
        this.title = title;
        this.author = author;
        this.description = description;
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

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
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
