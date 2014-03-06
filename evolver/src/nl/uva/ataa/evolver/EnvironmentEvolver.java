package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.uva.ataa.agent.genetic.EvaluateChromosome;
import nl.uva.ataa.agent.genetic.gene.WindGene;
import nl.uva.ataa.environment.EvolutionaryEnvironment;
import nl.uva.ataa.environment.Wind;

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

            final IChromosome sampleChromosome = new Chromosome(gaConf, new WindGene(gaConf), 8);

            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(poolSize);

            genotype = Genotype.randomInitialGenotype(gaConf);
            final Population population = genotype.getPopulation();
            for (int i = 0; i < genotype.getPopulation().size(); ++i) {
                IChromosome chromosome = population.getChromosome(i);
                WindGene[] genes = Arrays.copyOf(chromosome.getGenes(), chromosome.getGenes().length, WindGene[].class);

                WindGene[] windNS = Arrays.copyOfRange(genes, 0, 4);
                WindGene[] windEW = Arrays.copyOfRange(genes, 4, 8);

                mEnvironments.add(new EvolutionaryEnvironment(new Wind(windNS), new Wind(windEW)));
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
            // mEnvironments.get(i).setWeights(mGenotype.getPopulation().getChromosome(i));
        }
    }

    public List<EvolutionaryEnvironment> getEnvironments() {
        return mEnvironments;
    }

}
