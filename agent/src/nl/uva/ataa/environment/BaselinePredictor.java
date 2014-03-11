package nl.uva.ataa.environment;

import java.util.ArrayList;
import java.util.List;

public class BaselinePredictor extends Predictor {

    public enum Baseline {
        ONE(new double[] { 0.693851, 0.115374, 0.669592, 0.588185, 0.853936, 0.252569, 0.757596, 0.367433 }),
        TWO(new double[] { 0.720038, 0.635304, 0.044392, 0.648565, 0.132414, 0.259877, 0.086958, 0.813711 }),
        THREE(new double[] { 0.724593, 0.666699, 0.311691, 0.786064, 0.600669, 0.021126, 0.236896, 0.562895 }),
        FOUR(new double[] { 0.607132, 0.442880, 0.360980, 0.167207, 0.081301, 0.742238, 0.002850, 0.550981 }),
        FIVE(new double[] { 0.949278, 0.276789, 0.115042, 0.270776, 0.268197, 0.861445, 0.942433, 0.971455 }),
        SIX(new double[] { 0.550625, 0.867898, 0.916801, 0.643898, 0.211139, 0.425095, 0.758741, 0.897585 }),
        SEVEN(new double[] { 0.116730, 0.373797, 0.113577, 0.766653, 0.428122, 0.021471, 0.995174, 0.281151 }),
        EIGHT(new double[] { 0.680743, 0.024624, 0.224401, 0.606206, 0.739881, 0.397792, 0.043179, 0.897237 }),
        NINE(new double[] { 0.397997, 0.791233, 0.736065, 0.738457, 0.989597, 0.169351, 0.532904, 0.149639 }),
        TEN(new double[] { 0.187150, 0.631497, 0.268372, 0.896376, 0.190854, 0.726746, 0.766254, 0.929489 });

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
    public BaselinePredictor(final Baseline baselineNumber) {
        mBaselineNumber = baselineNumber;
    }

    @Override
    protected double getSample(final int index) {
        return mBaselineNumber.getIndex(index);
    }

    /**
     * Get a list of all baseline predictors
     * 
     * @return A list of all baseline predictors
     */
    public static List<BaselinePredictor> getAllBaselines() {
        List<BaselinePredictor> baselines = new ArrayList<>();

        baselines.add(new BaselinePredictor(Baseline.ONE));
        baselines.add(new BaselinePredictor(Baseline.TWO));
        baselines.add(new BaselinePredictor(Baseline.THREE));
        baselines.add(new BaselinePredictor(Baseline.FOUR));
        baselines.add(new BaselinePredictor(Baseline.FIVE));
        baselines.add(new BaselinePredictor(Baseline.SIX));
        baselines.add(new BaselinePredictor(Baseline.SEVEN));
        baselines.add(new BaselinePredictor(Baseline.EIGHT));
        baselines.add(new BaselinePredictor(Baseline.NINE));
        baselines.add(new BaselinePredictor(Baseline.TEN));

        return baselines;
    }
}
