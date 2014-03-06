package nl.uva.ataa.agent.genetic;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class EvaluateChromosome extends FitnessFunction {

    /**
     * Generated default serial UID
     */
    private static final long serialVersionUID = 3905984536032544547L;

    public EvaluateChromosome() {

    }

    @Override
    protected double evaluate(final IChromosome chromosome) {
        return chromosome.getFitnessValueDirectly();
    }
}
