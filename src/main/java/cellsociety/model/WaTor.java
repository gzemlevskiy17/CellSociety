package cellsociety.model;

/**
 * Wa-Tor World simulation.
 *
 * <p>
 * Uses three states: 0 (empty), 1 (tree), 2 (burning). Transition rules:
 * </p>
 * <ul>
 * <li>A burning cell becomes empty.</li>
 * <li>A tree catches fire if any neighbor is burning, or spontaneously with
 * probability {@code ignitionProbability}.</li>
 * <li>An empty cell grows a tree with probability {@code fillProbability}.</li>
 * </ul>
 *
 * <p>
 * <b>Assumptions:</b> Probabilities should be in the range [0.0, 1.0].
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> {@link Game}, {@link Grid}, {@link Cell}
 * </p>
 *
 * @author Joseph Wang
 * @see Game
 */

public class WaTor extends Game {

    private double pyRepro;
    private double pdrEner;
    private double pdrRepro;

    public WaTor(Grid grid, double pyRepro, double pdrEner, double pdrRepro) {
        super(grid);
        this.pyRepro = pyRepro;
        this. pdrEner = pdrEner;
        this. pdrRepro = pdrRepro;
    }

    @Override
    protected void updateGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

}
