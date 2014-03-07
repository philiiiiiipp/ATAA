package nl.uva.ataa.evolver;

import java.util.List;

import nl.uva.ataa.agent.genetic.evaluator.AgentChromosomeEvaluator;
import nl.uva.ataa.agent.genetic.gene.NeuralNetworkGene;
import nl.uva.ataa.environment.Predictor;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;

public class AgentEvolver {

    /** The currently used genotype */
    private final Genotype mGenotype;

    /** The currently used fitness function */
    private final AgentChromosomeEvaluator mFitnessFunction;

    /**
     * Creates an agent evolver
     * 
     * @param poolSize
     *            The amount of agents to evolve
     */
    public AgentEvolver(final int poolSize) {
        Genotype genotype = null;
        AgentChromosomeEvaluator evaluator = null;
        try {

            Configuration.reset();
            final Configuration gaConf = new DefaultConfiguration();
            gaConf.setPreservFittestIndividual(true);
            gaConf.setKeepPopulationSizeConstant(true);

            evaluator = new AgentChromosomeEvaluator();
            gaConf.setFitnessFunction(evaluator);

            final IChromosome sampleChromosome = new Chromosome(gaConf, new NeuralNetworkGene(gaConf),
                    AgentChromosomeEvaluator.getAgent().getWeights().length);

            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(poolSize);

            genotype = Genotype.randomInitialGenotype(gaConf);

        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }

        mGenotype = genotype;
        mFitnessFunction = evaluator;
    }

    /**
     * Evolve the current genotype
     * 
     * @param predictors
     *            The used environments to evolve the agents against
     * @return A list of evolved agents
     */
    public void evolveAgents(final List<Predictor> predictors) {
        mFitnessFunction.setPredictors(predictors);

        mGenotype.evolve();

        System.out.println(mFitnessFunction.getAverageSteps());
    }
}
