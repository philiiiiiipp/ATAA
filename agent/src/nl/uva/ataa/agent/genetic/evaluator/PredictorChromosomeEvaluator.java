package nl.uva.ataa.agent.genetic.evaluator;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.environment.WindEnvironment;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class PredictorChromosomeEvaluator extends FitnessFunction {

    private static final long serialVersionUID = 3905984536032544547L;

    private List<NeuroEvolutionaryAgent> mAgents = new ArrayList<>();

    public void setAgents(final List<NeuroEvolutionaryAgent> agents) {
        mAgents = agents;
    }

    @Override
    protected double evaluate(final IChromosome chromosome) {
        // TODO put the chromosome into the environment and evaluate it with the agents.

        final WindEnvironment environment = new WindEnvironment();
        return environment.getFitness();
    }
}
