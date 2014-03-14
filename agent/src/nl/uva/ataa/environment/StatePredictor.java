package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.utilities.Utilities;

import org.rlcommunity.environment.helicopter.HelicopterState;

/**
 * A predictor that uses beta destributions to generate the winds within environments. It can be
 * evolved.
 */
public class StatePredictor extends DiscretePredictor implements ChromosomeSpecimen {

    public static final double[] MODIFIERS = new double[] { HelicopterState.MAX_VEL, HelicopterState.MAX_POS,
            HelicopterState.MAX_RATE, HelicopterState.MAX_QUAT };

    public static final double BORDER_RANGE = 0.1;

    public static double sampleToParam(final int index, final double sample, final int numParamValues) {
        final double modifier = MODIFIERS[index / 3];
        final double discreteSample = Math.floor(sample * numParamValues) / (numParamValues - 1);
        return discreteSample * (2 - BORDER_RANGE * 2) * modifier - modifier + BORDER_RANGE * modifier;
    }

    /**
     * Creates a new predictor with random beta distributions.
     */
    public StatePredictor(final int numParamValues, final FitnessFunction fitnessFunction) {
        super(numParamValues, fitnessFunction);

        mDistributionWeights = new Double[18];
        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = Utilities.RNG.nextDouble();
        }
    }

    @Override
    public String env_init() {
        double envProbability = 1;
        final double[] startState = new double[12];
        for (int i = 0; i < 9; ++i) {
            final double sample = super.getSample(i);
            startState[i] = sampleToParam(i, sample, mNumParamValues);

            envProbability *= getDiscreteProbability(i, sample);
        }
        for (int i = 9; i < 12; ++i) {
            startState[i] = 0.5;
        }
        final double bias = Math.pow(1.0 / mNumParamValues, 9) / envProbability;

        setStartState(startState);

        return super.env_init() + " BIAS: " + bias;
    }

    @Override
    protected double getSample(final int index) {
        return 0.5;
    }
}
