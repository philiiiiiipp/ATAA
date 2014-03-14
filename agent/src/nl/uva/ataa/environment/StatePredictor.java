package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.utilities.Utilities;

import org.rlcommunity.environment.helicopter.HelicopterState;

/**
 * A predictor that uses beta destributions to generate the winds within environments. It can be evolved.
 */
public class StatePredictor extends DiscretePredictor implements ChromosomeSpecimen {

    public static final double[] MODIFIERS = new double[] { HelicopterState.MAX_VEL, HelicopterState.MAX_POS,
            HelicopterState.MAX_RATE, HelicopterState.MAX_QUAT };

    public static final double BORDER_RANGE = 0.1;

    /**
     * Creates a new predictor with random beta distributions.
     */
    public StatePredictor(final int numParamValues, final FitnessFunction fitnessFunction) {
        super(numParamValues, fitnessFunction);

        mDistributionWeights = new Double[24];
        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = Utilities.RNG.nextDouble();
        }
    }

    @Override
    public String env_init() {
        double envProbability = 1;
        final double[] startState = new double[12];
        for (int i = 0; i < 12; ++i) {
            final double modifier = MODIFIERS[i / 3];
            final double sample = super.getSample(i);
            startState[i] = sample * (2 - BORDER_RANGE * 2) * modifier - modifier + BORDER_RANGE;

            envProbability *= getDiscreteProbability(i, sample);
        }
        final double bias = Math.pow(1.0 / mNumParamValues, 12) / envProbability;

        setStartState(startState);

        return super.env_init() + " BIAS: " + bias;
    }

    @Override
    protected double getSample(final int index) {
        return 0.5;
    }
}
