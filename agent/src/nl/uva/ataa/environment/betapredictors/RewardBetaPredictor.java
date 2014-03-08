package nl.uva.ataa.environment.betapredictors;

import nl.uva.ataa.environment.BetaPredictor;

/**
 * A predictor that only takes the rewards into account to determine fitness.
 */
public class RewardBetaPredictor extends BetaPredictor {

    @Override
    public double getFitness() {
        return -getAccumulatedReward() / getNumEpisodes();
    }

}
