package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.genetic.EvaluateChromosome;
import nl.uva.ataa.agent.genetic.SuperGene;
import nl.uva.ataa.environment.EvolutionaryEnvironment;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;

public class EnvironmentEvolver {

    private final List<EvolutionaryEnvironment> mEnvironments = new ArrayList<>();

    private final Genotype mGenotype;

    public EnvironmentEvolver(final int poolSize) {
        Genotype genotype = null;

        try {

            Configuration.reset();
            final Configuration gaConf = new DefaultConfiguration();
            gaConf.setPreservFittestIndividual(true);
            gaConf.setKeepPopulationSizeConstant(true);
            gaConf.setFitnessFunction(new EvaluateChromosome());

            for (int i = 0; i < poolSize; i++) {
                mEnvironments.add(new EvolutionaryEnvironment());
            }

            final IChromosome sampleChromosome = new Chromosome(gaConf, new SuperGene(gaConf), mEnvironments.get(0)
                    .getWeights().length);

            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(poolSize);

            genotype = Genotype.randomInitialGenotype(gaConf);
            final Population population = genotype.getPopulation();
            for (int i = 0; i < genotype.getPopulation().size(); ++i) {
                mEnvironments.get(i).setWeights(population.getChromosome(i));
            }

        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }

        mGenotype = genotype;
    }

    public void evolveEnvironments() {

        for (int i = 0; i < mEnvironments.size(); ++i) {
            mGenotype.getPopulation().getChromosome(i).setFitnessValue(mEnvironments.get(i).getFitness());
        }

        mGenotype.evolve();

        for (int i = 0; i < mEnvironments.size(); ++i) {
            mEnvironments.get(i).setWeights(mGenotype.getPopulation().getChromosome(i));
        }
    }

    public List<EvolutionaryEnvironment> getEnvironments() {
        return mEnvironments;
    }

}
