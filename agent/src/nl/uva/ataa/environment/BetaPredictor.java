package nl.uva.ataa.environment;

import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.distribution.BetaDistribution;

public class BetaPredictor extends Predictor {

    /** The weights representing 8 distributions */
    private final double[] mDistributionWeights = new double[16];

    /**
     * Creates a new predictor with random beta distributions.
     */
    public BetaPredictor() {
        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = Utilities.RNG.nextDouble();
        }
    }

    @Override
    protected double getSample(final int index) {
        final int weightIndex = index * 2;
        return getSample(mDistributionWeights[weightIndex], mDistributionWeights[weightIndex + 1]);
    }

    private double getSample(final double alpha, final double beta) {
        final BetaDistribution betaDist = new BetaDistribution(alpha, beta);
        return betaDist.cumulativeProbability(Utilities.RNG.nextDouble());
    }
}
