package nl.uva.ataa.environment.fitness;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function that only takes the rewards into account to determine fitness.
 */
public class RewardFitness implements FitnessFunction {

    @Override
    public double getFitness(final Predictor predictor) {
        return -predictor.getAccumulatedReward() / predictor.getNumEpisodes();
    }

}
