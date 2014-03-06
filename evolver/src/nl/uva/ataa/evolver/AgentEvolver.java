package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.agent.genetic.EvaluateChromosome;
import nl.uva.ataa.agent.genetic.SuperGene;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;

public class AgentEvolver {

    private final List<NeuralNetworkAgent> mAgents = new ArrayList<>();

    private final Genotype mGenotype;

    public AgentEvolver(final int poolSize) {
        Genotype genotype = null;

        try {

            Configuration.reset();
            Configuration gaConf = new DefaultConfiguration();
            gaConf.setPreservFittestIndividual(true);
            gaConf.setKeepPopulationSizeConstant(true);
            gaConf.setFitnessFunction(new EvaluateChromosome());

            for (int i = 0; i < poolSize; i++) {
                NeuroEvolutionaryAgent nAgent = new NeuroEvolutionaryAgent();
                mAgents.add(nAgent);
            }

            IChromosome sampleChromosome = new Chromosome(gaConf, new SuperGene(gaConf),
                    mAgents.get(0).getWeights().length);

            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(poolSize);

            genotype = Genotype.randomInitialGenotype(gaConf);
            Population population = genotype.getPopulation();
            for (int i = 0; i < genotype.getPopulation().size(); ++i) {
                mAgents.get(i).setWeights(population.getChromosome(i));
            }

        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        mGenotype = genotype;
    }

    public void evolveAgents() {

        for (int i = 0; i < mAgents.size(); ++i) {
            // mGenotype.getPopulation().getChromosome(i).setFitnessValue(mAgents.get(i).getFitness());
        }

        mGenotype.evolve();

        for (int i = 0; i < mAgents.size(); ++i) {
            mAgents.get(i).setWeights(mGenotype.getPopulation().getChromosome(i));
        }
    }

    public List<NeuralNetworkAgent> getAgents() {
        return mAgents;
    }

}
