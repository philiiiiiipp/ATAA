package nl.uva.agent.genetic;

import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;

public class GeneticModelCreator {

	private final List<IChromosome> mFittestChromosomes = new ArrayList<>();

	private final int mEvolutions;

	/** The standard population size */
	public final int STANDARD_POP_SIZE = 20;

	/** Amount of evolutions */
	public final int STANDARD_EVOLUTIONS = 20;

	/**
	 * Adds all previously calculated fittest chromosomes at evolution step
	 * STANDARD_EVOLUTIONS + ADD_BEST_CHROMOSOMES_TIME
	 */
	public final int ADD_BEST_CHROMOSOMES_TIME = -5;

	/** The currently calculated genotype */
	private Genotype mGenotype;

	/**
	 * Create a genetic model creator with standard values and the given river
	 * 
	 * @param river
	 *            The river as the basis of the model
	 */
	public GeneticModelCreator() {
		mEvolutions = STANDARD_EVOLUTIONS;
	}

	/**
	 * Initialize a genotype with the given parameters
	 * 
	 * @param populationSize
	 *            The size of the population
	 * @return The generated genotype
	 */
	private Genotype initialiseGenotype(final int populationSize,
			final int geneNumber) {

		Configuration.reset();
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		Genotype genotype = null;
		try {
			IChromosome sampleChromosome = new Chromosome(gaConf,
					new SuperGene(gaConf), geneNumber);

			gaConf.setAlwaysCaculateFitness(true);
			gaConf.setSampleChromosome(sampleChromosome);
			gaConf.setPopulationSize(populationSize);

			// Set a fitness function!
			// gaConf.setFitnessFunction(new EvaluateModel(mStates, mActions,
			// river));

			genotype = Genotype.randomInitialGenotype(gaConf);

		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		return genotype;
	}

}
