package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;

public class DiscretePredictor extends BetaPredictor {

    final int mNumParamValues;

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
}
