package nl.uva.ataa.environment;

import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.evolver.genetic.DoubleLimitChromosome;
import nl.uva.ataa.evolver.genetic.DoubleLimitMutationPolicy;
import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * A predictor that uses beta destributions to generate the winds within environments. It can be evolved.
 */
public class DiscreteWindPredictor extends Predictor implements ChromosomeSpecimen {

    /** The weights representing 8 distributions */
    public Double[] mDistributionWeights = new Double[10];

    public enum Samples {
        ONE(new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 }),
        TWO(new double[] { 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2 }),
        THREE(new double[] { 0.1, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.2 }),
        FOUR(new double[] { 0.1, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.1 }),
        FIVE(new double[] { 0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 0.1, 0.2 }),
        SIX(new double[] { 0.2, 0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 0.1 }),
        SEVEN(new double[] { 0.2, 0.3, 0.1, 0.1, 0.1, 0.1, 0.2, 0.2 }),
        EIGHT(new double[] { 0.2, 0.2, 0.3, 0.1, 0.3, 0.1, 0.1, 0.1 }),
        NINE(new double[] { 0.1, 0.3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 }),
        TEN(new double[] { 0.9, 0.5, 0.9, 0.9, 0.9, 0.3, 0.9, 0.9 });

        private double[] mSample;

        private Samples(final double[] sample) {
            mSample = sample;
        }

        public double getIndex(final int index) {
            return mSample[index];
        }
    }

    private Samples selectedEnvironment;

    private final FitnessFunction mFitnessFunction;

    /**
     * Creates a new predictor with random beta distributions.
     */
    public DiscreteWindPredictor(final FitnessFunction fitnessFunction) {
        mFitnessFunction = fitnessFunction;

        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = 0.5;
        }
    }

    @Override
    public double getFitness() {
        return mFitnessFunction.getFitness(this);
    }

    @Override
    protected double getSample(final int index) {
        if (index == 0) {
            double weightSum = 0;
            for (final double weight : mDistributionWeights) {
                weightSum += weight;
            }

            final double selection = Utilities.RNG.nextDouble() * weightSum;
            double selectionCount = 0;
            for (int i = 0;; ++i) {
                selectionCount += mDistributionWeights[i];
                if (selectionCount >= selection) {
                    selectedEnvironment = Samples.values()[i];
                    break;
                }
            }
        }
        return selectedEnvironment.getIndex(index);
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
