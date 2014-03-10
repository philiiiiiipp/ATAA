package nl.uva.ataa.environment.fitness;

import java.util.List;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function that takes the skewness of the rewards into account to determine fitness.
 * 
 * Skewness is a measure of the asymmetry of the probability distribution.
 * 
 * Negative skew indicates that the tail on the left side of the probability density function is
 * longer or fatter than the right side.
 */
public class SkewnessFitness extends VarianceFitness {

    @Override
    public double getFitness(final Predictor predictor) {
        double numerator = 0;
        double sum = 0;
        double mean = 0;
        final int n = predictor.getNumEpisodes();

        final List<Double> episodeRewards = predictor.getEpisodeRewards();

        for (double i : episodeRewards) {
            sum += i;
        }

        mean = sum / n;

        final double sd = Math.sqrt(getPredictorVariance(predictor));

        for (double i : episodeRewards) {
            numerator += Math.pow((i - mean), 3);
        }

        return (numerator / n) / Math.pow(sd, 1.5);
    }

}
