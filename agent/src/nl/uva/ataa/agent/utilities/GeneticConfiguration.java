package nl.uva.ataa.agent.utilities;

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;

public class GeneticConfiguration {

    private static final int POPULATION_SIZE = 50;

    public static Configuration generateConfiguration(final IChromosome sampleChromosome) {
        Configuration.reset();
        Configuration gaConf = new DefaultConfiguration();
        gaConf.setPreservFittestIndividual(true);
        gaConf.setKeepPopulationSizeConstant(true);
        try {
            gaConf.setSampleChromosome(sampleChromosome);
            gaConf.setPopulationSize(POPULATION_SIZE);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return gaConf;
    }
}
