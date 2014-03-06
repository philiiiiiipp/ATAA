package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.agent.genetic.evaluator.PolicyChromosomeEvaluator;
import nl.uva.ataa.agent.genetic.gene.NeuralNetworkGene;
import nl.uva.ataa.environment.EvolutionaryEnvironment;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;

public class AgentEvolver {

    /** List of agents to save computation time to create the neural network */
    private final List<NeuralNetworkAgent> mAgents = new ArrayList<>();

    /** The currently used genotype */
    private final Genotype mGenotype;

    /** The currently used fitness function */
    private final PolicyChromosomeEvaluator mFitnessFunction;

    /**
     * Creates an agent evolver
     * 
     * @param poolSize
     *            The amount of agents to evolve
     */
    public AgentEvolver(final int poolSize) {
        Genotype genotype = null;
        PolicyChromosomeEvaluator evaluator = null;
        try {

            Configuration.reset();
            final Configuration gaConf = new DefaultConfiguration();
            gaConf.setPreservFittestIndividual(true);
            gaConf.setKeepPopulationSizeConstant(true);

            evaluator = new PolicyChromosomeEvaluator();
            gaConf.setFitnessFunction(evaluator);

            for (int i = 0; i < poolSize; i++) {
                final NeuroEvolutionaryAgent nAgent = new NeuroEvolutionaryAgent();
                mAgents.add(nAgent);
            }

            final IChromosome sampleChromosome = new Chromosome(gaConf, new NeuralNetworkGene(gaConf), mAgents.get(0)
                    .getWeights().length);

            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(poolSize);

            genotype = Genotype.randomInitialGenotype(gaConf);
            final Population population = genotype.getPopulation();
            for (int i = 0; i < genotype.getPopulation().size(); ++i) {
                mAgents.get(i).setWeights(population.getChromosome(i));
            }

        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }

        mGenotype = genotype;
        mFitnessFunction = evaluator;
    }

    /**
     * Evolve the current genotype
     * 
     * @param environments
     *            The used environments to evolve the agents against
     * @return A list of evolved agents
     */
    public List<NeuralNetworkAgent> evolveAgents(final List<EvolutionaryEnvironment> environments) {
        mFitnessFunction.setEnvironments(environments);

        mGenotype.evolve();

        for (int i = 0; i < mAgents.size(); ++i) {
            mAgents.get(i).setWeights(mGenotype.getPopulation().getChromosome(i));
        }

        return mAgents;
    }

    /**
     * Get all agents of the last evolution
     * 
     * @return A list of agents
     */
    public List<NeuralNetworkAgent> getAgents() {
        return mAgents;
    }
}
