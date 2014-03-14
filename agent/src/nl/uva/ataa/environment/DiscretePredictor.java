package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;

/**
 * A predictor that uses beta destributions to generate the winds within environments. It can be evolved.
 */
public class DiscretePredictor extends BetaPredictor {

    final int mNumParamValues;

    /**
     * Creates a new predictor with random beta distributions.
     * 
     * @param numParamValues
     *            The amount of discrete values per parameters
     * @param fitnessFunction
     *            The fitness function used to evolve the predictor
     */
    public DiscretePredictor(final int numParamValues, final FitnessFunction fitnessFunction) {
        super(fitnessFunction);

        mNumParamValues = numParamValues;
    }

    @Override
    protected double getSample(final int index) {
        return Math.floor(super.getSample(index) * mNumParamValues) / (mNumParamValues - 1);
    }

    protected double getDiscreteProbability(final int index, final double value) {
        final double min = value * (mNumParamValues - 1) / mNumParamValues;
        final double max = min + 1.0 / mNumParamValues;

        return getOccuranceProbability(index, min, max);
    }

    protected double getBias(final int index, final double value) {
        return 1 / (getDiscreteProbability(index, value) * mNumParamValues);
    }
}
