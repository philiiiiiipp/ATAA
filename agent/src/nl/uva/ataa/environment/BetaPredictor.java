package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.evolver.genetic.DoubleLimitChromosome;
import nl.uva.ataa.evolver.genetic.DoubleLimitMutationPolicy;
import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * A predictor that uses beta destributions to generate the winds within environments. It can be evolved.
 */
public class BetaPredictor extends Predictor implements ChromosomeSpecimen {

    /** The weights representing 8 distributions */
    private Double[] mWeights;

    private BetaDistribution[] mDistributions;

    private final FitnessFunction mFitnessFunction;

    /**
     * Creates a new predictor with random beta distributions.
     */
    public BetaPredictor(final FitnessFunction fitnessFunction) {
        mFitnessFunction = fitnessFunction;

        final Double[] weights = new Double[16];
        for (int i = 0; i < weights.length; ++i) {
            weights[i] = Utilities.RNG.nextDouble();
        }

        setWeights(weights);
    }

    @Override
    public double getFitness() {
        return mFitnessFunction.getFitness(this);
    }

    @Override
    protected double getSample(final int index) {
        return mDistributions[index].cumulativeProbability(Utilities.RNG.nextDouble());
    }

    protected double getOccuranceProbability(final int index, final double min, final double max) {
        final double xMin = mDistributions[index].inverseCumulativeProbability(min);
        final double xMax = mDistributions[index].inverseCumulativeProbability(max);
        return xMax - xMin;
    }

    public void setWeights(final Double[] weights) {
        mWeights = weights;
        mDistributions = new BetaDistribution[mWeights.length];

        for (int i = 0; i < weights.length / 2; ++i) {
            mDistributions[i] = new BetaDistribution(mWeights[i], mWeights[i + 1]);
        }
    }

    @Override
    public Chromosome getChromosome() {
        final double fitness = (getNumEpisodes() > 0 ? getFitness() : ChromosomeSpecimen.NO_FITNESS);
        return new DoubleLimitChromosome(mWeights, fitness, 0, 1, false, true);
    }

    @Override
    public void setChromosome(final Chromosome chromosome) {
        setWeights(((DoubleLimitChromosome) chromosome).getWeights());
    }

    @Override
    public MutationPolicy getMutationPolicy(final double mutationRange) {
        return new DoubleLimitMutationPolicy(mutationRange);
    }
}
