package nl.uva.ataa.environment.betapredictors;

import java.util.List;

import nl.uva.ataa.environment.BetaPredictor;

/**
 * A predictor that only takes the variance between rewards into account to determine fitness.
 */
public class VarianceBetaPredictor extends BetaPredictor {

    @Override
    public double getFitness() {
        final List<Double> episodeRewards = getEpisodeRewards();
        for (final Double episodeReward : episodeRewards) {
            // TODO: Do some variance stuff here
        }

        return -getAccumulatedReward() / getNumEpisodes();
    }

}
