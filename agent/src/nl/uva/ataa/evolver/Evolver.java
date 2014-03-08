package nl.uva.ataa.evolver;

import java.util.LinkedList;
import java.util.List;

import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.utilities.Conf;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.ListPopulation;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;

public abstract class Evolver<T extends ChromosomeSpecimen> {

    /** The specimens that will be evolved */
    private final List<T> mSpecimens = new LinkedList<>();

    /** The algorithm performing the evolutions */
    private final GeneticAlgorithm mGeneticAlgorithm;

    /** How many best chromosomes will be directly transferred to the next generation [in %] */
    private final double mElitismRate;

    /**
     * Prepares the new evolver with the given parameters.
     * 
     * @param poolSize
     *            The amount of specimens to evolve
     * @param crossoverRate
     *            The chance that two chromosomes will mate
     * @param crossoverRatio
     *            The ratio between parents' genes during mating
     * @param mutationRate
     *            The range that a gene can maximaly mutate
     * @param mutationRange
     *            The range that a gene can maximaly mutate
     * @param tournamentSize
     *            The amount of chromosomes that compete for reproduction
     * @param elitismRate
     *            How many best chromosomes will be directly transferred to the next generation [in %]
     * @param conf
     *            The configuration for getting specimens
     */
    protected Evolver(final int poolSize, final double crossoverRate, final double crossoverRatio,
            final double mutationRate, final double mutationRange, final int tournamentSize, final double elitismRate,
            final Conf conf) {
        for (int i = 0; i < poolSize; i++) {
            mSpecimens.add(getSpecimen(conf));
        }

        mGeneticAlgorithm = new GeneticAlgorithm(new UniformCrossover<Object>(crossoverRatio), crossoverRate,
                mSpecimens.get(0).getMutationPolicy(mutationRange), mutationRate, new TournamentSelection(
                        tournamentSize));
        mElitismRate = elitismRate;
    }

    /**
     * Creates an initial specimen to start the evolver with.
     * 
     * @param conf
     *            The configuration given in the constructor
     * 
     * @return A random of specimen
     */
    protected abstract T getSpecimen(Conf conf);

    /**
     * Evolves the current set of specimens based on performance during previous tests.
     */
    public void evolve() {
        // Convert agents to chromosomes
        final ListPopulation population = new ElitisticListPopulation(mSpecimens.size(), mElitismRate);
        for (final T specimen : mSpecimens) {
            final Chromosome chromosome = specimen.getChromosome();
            if (chromosome.getFitness() != ChromosomeSpecimen.NO_FITNESS) {
                population.addChromosome(chromosome);
            }
        }

        // Evolve the chromosomes
        final ListPopulation newPopulation = (ListPopulation) mGeneticAlgorithm.nextGeneration(population);

        // Apply chromosomes to agents
        int i = 0;
        for (final Chromosome chromosome : newPopulation.getChromosomes()) {
            mSpecimens.get(i++).setChromosome(chromosome);
        }
    }

    /**
     * @return The specimens that will be evolved
     */
    public List<T> getSpecimens() {
        return mSpecimens;
    }

    /**
     * @return The average fitness of the specimens determined by previously executed tests.
     */
    public double getAverageFitness() {
        double fitness = 0;
        for (final T specimen : mSpecimens) {
            fitness += specimen.getFitness();
        }
        return fitness / mSpecimens.size();
    }
}
