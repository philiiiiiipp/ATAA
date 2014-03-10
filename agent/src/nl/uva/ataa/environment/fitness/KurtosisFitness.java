package nl.uva.ataa.environment.fitness;

import java.util.List;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function that takes the kurtosis of the rewards into account to determine fitness.
 * 
 * Higher kurtosis means more of the variance is the result of infrequent extreme deviations, as
 * opposed to frequent modestly sized deviations.
 * 
 * The "minus 3" at the end of this formula is often explained as a correction to make the kurtosis
 * of the normal distribution equal to zero.
 */

public class KurtosisFitness extends VarianceFitness {

    @Override
    public double getFitness(final Predictor predictor) {
        double sum = 0;
        double mean = 0;

        double numerator = 0;
        double denominator = 0;

        final int n = predictor.getNumEpisodes();

        final double sd = Math.sqrt(getPredictorVariance(predictor));

        final List<Double> episodeRewards = predictor.getEpisodeRewards();

        for (double i : episodeRewards) {
            sum += i;
        }

        mean = sum / n;

        for (double i : episodeRewards) {
            numerator += Math.pow((i - mean), 4);
            denominator += Math.pow((1 / n * Math.pow(sd, 2)), 2);
        }

        return (numerator / n) / denominator - 3;
    }
}
