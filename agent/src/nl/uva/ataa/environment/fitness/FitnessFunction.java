package nl.uva.ataa.environment.fitness;

import nl.uva.ataa.environment.Predictor;

public interface FitnessFunction {

    double getFitness(Predictor predictor);
}
