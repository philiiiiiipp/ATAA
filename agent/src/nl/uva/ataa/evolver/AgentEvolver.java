package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.evolver.genetic.DoubleChromosome;
import nl.uva.ataa.evolver.genetic.DoubleChromosomePopulation;
import nl.uva.ataa.evolver.genetic.DoubleMutationPolicy;

import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;

public class AgentEvolver {

    /** The chance that two chromosomes will mate */
    private static final double CROSSOVER_RATE = 0.8;
    /** The ratio between parents' genes during mating */
    private static final double CROSSOVER_RATIO = 0.8;

    /** The chance that a chromosome will mutate one of its genes */
    private static final double MUTATION_RATE = 0.5;
    /** The range that a gene can maximaly mutate */
    private static final double MUTATION_RANGE = 1.5;

    /** The amount of chromosomes that compete for reproduction */
    private static final int TOURNAMENT_SIZE = 5;

    /** The agents that will be evolved */
    private final List<ShimonsAgent> mAgents = new ArrayList<>();

    /** The algorithm performing the evolutions */
    final GeneticAlgorithm mGeneticAlgorithm = new GeneticAlgorithm(new UniformCrossover<DoubleChromosome>(
            CROSSOVER_RATIO), CROSSOVER_RATE, new DoubleMutationPolicy(MUTATION_RANGE), MUTATION_RATE,
            new TournamentSelection(TOURNAMENT_SIZE));

    /**
     * Creates an evolver with random agents.
     * 
     * @param poolSize
     *            The amount of agents to evolve
     */
    public AgentEvolver(final int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            mAgents.add(new ShimonsAgent());
        }
    }

    /**
     * Evolves the current set of agents based on performance during previous tests.
     */
    public void evolveAgents() {
        // Convert agents to chromosomes
        final DoubleChromosomePopulation population = new DoubleChromosomePopulation(mAgents.size());
        for (final ShimonsAgent agent : mAgents) {
            population.addChromosome(new DoubleChromosome(agent.getWeights(), agent.getFitness()));
        }

        // Evolve the chromosomes
        final DoubleChromosomePopulation newPopulation = (DoubleChromosomePopulation) mGeneticAlgorithm
                .nextGeneration(population);

        // Apply chromosomes to agents
        int i = 0;
        for (final DoubleChromosome chromosome : newPopulation.getDoubleChromosomes()) {
            mAgents.get(i++).setWeights(chromosome.getWeights());
        }
    }

    /**
     * @return The agents that will be evolved
     */
    public List<ShimonsAgent> getAgents() {
        return mAgents;
    }

    /**
     * @return The average fitness of the agents determined by previously executed tests.
     */
    public double getAverageFitness() {
        double fitness = 0;
        for (final ShimonsAgent agent : mAgents) {
            fitness += agent.getFitness();
        }
        return fitness / mAgents.size();
    }

    /**
     * @return The average number of steps taken by the agents during previously executed tests.
     */
    public double getAverageNumSteps() {
        double numSteps = 0;
        for (final ShimonsAgent agent : mAgents) {
            numSteps += agent.getAverageNumSteps();
        }
        return numSteps / mAgents.size();
    }

}
