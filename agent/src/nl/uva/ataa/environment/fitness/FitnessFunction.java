package nl.uva.ataa.environment.fitness;

import nl.uva.ataa.environment.Predictor;

/**
 * A fitness function to evaluate predictor performance with.
 */
public interface FitnessFunction {

    /**
     * @return The fitness of the environment based on the last series of tests it has performed.
     */
    double getFitness(Predictor predictor);
}
