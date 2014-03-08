package nl.uva.ataa.environment.fitness;

import java.util.List;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function that takes the variance between rewards into account to determine fitness.
 */
public class VarianceFitness implements FitnessFunction {

    @Override
    public double getFitness(final Predictor predictor) {
        final List<Double> episodeRewards = predictor.getEpisodeRewards();

        return -predictor.getAccumulatedReward() / predictor.getNumEpisodes();
    }

}
