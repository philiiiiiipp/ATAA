package nl.uva.ataa.evolver.genetic;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * An inferface for specimens whose behaviour is affected by the chromosomes given to them.
 */
public interface ChromosomeSpecimen {

    /** Fitness used when the fitness has not been determined */
    public static final double NO_FITNESS = Double.NEGATIVE_INFINITY;

    /**
     * @return The fitness of the environment based on the last series of tests it has performed.
     */
    double getFitness();

    /**
     * @return A chromosome representation of the specimen's charactaristics. The fitness must be NO_FITNESS if the
     *         fitness could not be properly estimated.
     */
    Chromosome getChromosome();

    /**
     * Sets the specimen's genes based on the chromosome given to them.
     * 
     * @param chromosome
     *            The chromosome to apply
     */
    void setChromosome(Chromosome chromosome);

    /**
     * Constructs a mutation policy that can be applied to the specimen's chromosome type.
     * 
     * @param mutationRange
     *            The range that a gene can maximally mutate
     * 
     * @return A new mutation policy
     */
    MutationPolicy getMutationPolicy(double mutationRange);
}
