package nl.uva.ataa.environment;

import java.util.ArrayList;
import java.util.List;

public class StateBaselinePredictor extends Predictor {

    public enum Baseline {
        ONE(new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 }),
        TWO(new double[] { 0.1, 0.6, 0.2, 0.0, 0.1, 0.5, 0.7, 0.2, 0.7, 0.2, 0.1, 0.3 }),
        THREE(new double[] { 0.4, 0.7, 0.7, 0.22, 0.5, 0.7, 0.6, 0.9, 0.2, 0.3, 0.2, 0.8 }),
        FOUR(new double[] { 0.5, 0.8, 0.4, 0.2, 0.5, 0.1, 0.0, 0.5, 0.5, 0.0, 0.4, 0.01 }),
        FIVE(new double[] { 0.7, 0.6, 0.32, 0.7, 0.1, 0.7, 0.8, 0.5, 0.3, 0.7, 0.3, 0.1 }),
        SIX(new double[] { 0.2, 0.4, 2, 0.2, 0.2, 0.5, 0.3, 0.4, 0.1, 0.0, 0.4, 0.4 }),
        SEVEN(new double[] { 0.7, 0.2, 0.6, 0.8, 0.3, 0.7, 0.7, 0.52, 0.3, 0.4, 0.0, 0.7 }),
        EIGHT(new double[] { 0.3, 0.8, 0.2, 0.2, 0.6, 0.4, 0.7, 0.4, 0.2, 0.6, 0.2, 0.5 }),
        NINE(new double[] { 0.5, 0.6, 0.9, 0.7, 0.7, 0.55, 0.7, 0.3, 0.7, 0.3, 0.8, 0.9 }),
        TEN(new double[] { 0.1, 0.1, 0.5, 0.8, 0.7, 0.4, 0.7, 0.3, 0.3, 0.6, 0.5, 0.3 });

        private double[] mBaseline;

        private Baseline(final double[] baseline) {
            mBaseline = baseline;
        }

        public double getIndex(final int index) {
            return mBaseline[index];
        }
    }

    /** The baseline of this baselinepredictor */
    private final Baseline mBaselineNumber;

    /**
     * Construct a baseline predictor with the given baseline
     * 
     * @param baselineNumber
     *            The baseline
     */
    public StateBaselinePredictor(final Baseline baselineNumber) {
        mBaselineNumber = baselineNumber;
    }

    @Override
    public String env_init() {
        final double[] startState = new double[12];
        for (int i = 0; i < 12; ++i) {
            final double modifier = StatePredictor.MODIFIERS[i / 3];
            startState[i] = mBaselineNumber.getIndex(i) * (2 - StatePredictor.BORDER_RANGE * 2) * modifier - modifier
                    + StatePredictor.BORDER_RANGE;
        }

        setStartState(startState);

        return super.env_init();
    }

    @Override
    protected double getSample(final int index) {
        return 0.5;
    }

    /**
     * Get a list of all baseline predictors
     * 
     * @return A list of all baseline predictors
     */
    public static List<StateBaselinePredictor> getAllBaselines() {
        final List<StateBaselinePredictor> baselines = new ArrayList<>();

        baselines.add(new StateBaselinePredictor(Baseline.ONE));
        baselines.add(new StateBaselinePredictor(Baseline.TWO));
        baselines.add(new StateBaselinePredictor(Baseline.THREE));
        // baselines.add(new StateBaselinePredictor(Baseline.FOUR));
        // baselines.add(new StateBaselinePredictor(Baseline.FIVE));
        // baselines.add(new StateBaselinePredictor(Baseline.SIX));
        baselines.add(new StateBaselinePredictor(Baseline.SEVEN));
        // baselines.add(new StateBaselinePredictor(Baseline.EIGHT));
        baselines.add(new StateBaselinePredictor(Baseline.NINE));
        // baselines.add(new StateBaselinePredictor(Baseline.TEN));

        return baselines;
    }
}
