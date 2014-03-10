package nl.uva.ataa.evolver;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.utilities.Conf;

public class AgentEvolver extends Evolver<ShimonsAgent> {

    /** The chance that two chromosomes will mate */
    private static final double CROSSOVER_RATE = 0.8;
    /** The ratio between parents' genes during mating */
    private static final double CROSSOVER_RATIO = 0.8;

    /** The chance that a chromosome will mutate one of its genes */
    private static final double MUTATION_RATE = 0.5;
    /** The range that a gene can maximally mutate */
    private static final double MUTATION_RANGE = 1.5;

    /** The amount of chromosomes that compete for reproduction */
    private static final int TOURNAMENT_SIZE = 5;
    /** How many best chromosomes will be directly transferred to the next generation [in %] */
    private static final double ELITISM_RATE = 0.01;

    /**
     * Creates an evolver with random agents.
     * 
     * @param poolSize
     *            The amount of agents to evolve
     */
    public AgentEvolver(final int poolSize) {
        super(poolSize, CROSSOVER_RATE, CROSSOVER_RATIO, MUTATION_RATE, MUTATION_RANGE, TOURNAMENT_SIZE, ELITISM_RATE,
                null);
    }

    @Override
    protected ShimonsAgent getSpecimen(final Conf conf) {
        return new ShimonsAgent();
    }

    /**
     * @return The average number of steps taken by the agents during previously executed tests.
     */
    public double getAverageNumSteps() {
        double numSteps = 0;
        for (final ShimonsAgent agent : getSpecimens()) {
            numSteps += agent.getAverageNumSteps();
        }
        return numSteps / getSpecimens().size();
    }

}
