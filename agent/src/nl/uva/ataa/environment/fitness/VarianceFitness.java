package nl.uva.ataa.environment.fitness;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function that takes the variance between rewards into account to determine fitness.
 */
public class VarianceFitness implements FitnessFunction {

    @Override
    public double getFitness(final Predictor predictor) {
        return getPredictorVariance(predictor);
    }

    public double getPredictorVariance(final Predictor predictor) {
        double sum = 0;
        double var = 0;
        double mean = 0;
        final int n = predictor.getNumEpisodes();

        for (double i : predictor.getEpisodeRewards()) {
            sum += i;
        }

        mean = sum / n;

        for (double i : predictor.getEpisodeRewards()) {
            var += Math.pow((i - mean), 2);
        }
        return var / n;
    }
}
