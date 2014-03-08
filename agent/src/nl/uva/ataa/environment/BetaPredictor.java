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
    public Double[] mDistributionWeights = new Double[16];

    private final FitnessFunction mFitnessFunction;

    /**
     * Creates a new predictor with random beta distributions.
     */
    public BetaPredictor(final FitnessFunction fitnessFunction) {
        mFitnessFunction = fitnessFunction;

        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = Utilities.RNG.nextDouble();
        }
    }

    @Override
    public double getFitness() {
        return mFitnessFunction.getFitness(this);
    }

    @Override
    protected double getSample(final int index) {
        final int weightIndex = index * 2;
        return getSample(mDistributionWeights[weightIndex], mDistributionWeights[weightIndex + 1]);
    }

    private double getSample(final double alpha, final double beta) {
        final BetaDistribution betaDist = new BetaDistribution(alpha, beta);
        return betaDist.cumulativeProbability(Utilities.RNG.nextDouble());
    }

    @Override
    public Chromosome getChromosome() {
        final double fitness = (getNumEpisodes() > 0 ? getFitness() : ChromosomeSpecimen.NO_FITNESS);
        return new DoubleLimitChromosome(mDistributionWeights, fitness, 0, 1, false, true);
    }

    @Override
    public void setChromosome(final Chromosome chromosome) {
        mDistributionWeights = ((DoubleLimitChromosome) chromosome).getWeights();
    }

    @Override
    public MutationPolicy getMutationPolicy(final double mutationRange) {
        return new DoubleLimitMutationPolicy(mutationRange);
    }
}
