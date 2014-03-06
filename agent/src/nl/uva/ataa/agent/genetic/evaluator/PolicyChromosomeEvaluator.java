package nl.uva.ataa.agent.genetic.evaluator;

import java.util.List;

import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.environment.EvolutionaryEnvironment;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class PolicyChromosomeEvaluator extends FitnessFunction {

    /**
     * Generated default serial UID
     */
    private static final long serialVersionUID = 3905984536032544547L;

    private static final NeuroEvolutionaryAgent mTestAgent = new NeuroEvolutionaryAgent();

    public void setEnvironments(final List<EvolutionaryEnvironment> environments) {
        mTestAgent.setEnvironments(environments);
    }

    @Override
    protected double evaluate(final IChromosome chromosome) {
        mTestAgent.setWeights(chromosome);
        return mTestAgent.getFitness();
    }
}
