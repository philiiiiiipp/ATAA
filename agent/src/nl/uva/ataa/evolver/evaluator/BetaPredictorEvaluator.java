package nl.uva.ataa.evolver.evaluator;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.environment.BetaPredictor;
import nl.uva.ataa.environment.WindEnvironment;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class BetaPredictorEvaluator extends FitnessFunction {

    private static final long serialVersionUID = 3905984536032544547L;

    private List<NeuroEvolutionaryAgent> mAgents = new ArrayList<>();

    public void setAgents(final List<NeuroEvolutionaryAgent> agents) {
        mAgents = agents;
    }

    @Override
    protected double evaluate(final IChromosome chromosome) {
        final BetaPredictor predictor = new BetaPredictor(chromosome);

        final WindEnvironment environment = predictor.generateEnvironment();
        return environment.getFitness();
    }
}
