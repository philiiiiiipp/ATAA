package nl.uva.ataa.agent.genetic.evaluator;

import java.util.List;

import nl.uva.ataa.agent.NeuroEvolutionaryAgent;
import nl.uva.ataa.environment.Predictor;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class PolicyChromosomeEvaluator extends FitnessFunction {

    /**
     * Generated default serial UID
     */
    private static final long serialVersionUID = 3905984536032544547L;

    private static final NeuroEvolutionaryAgent sTestAgent = new NeuroEvolutionaryAgent();

    private double mFitnessSum = 0;
    private int mNumStepsSum = 0;
    private int mNumTests = 0;

    public static NeuroEvolutionaryAgent getAgent() {
        return sTestAgent;
    }

    public void setPredictors(final List<Predictor> predictors) {
        sTestAgent.setPredictors(predictors);
    }

    @Override
    protected double evaluate(final IChromosome chromosome) {
        sTestAgent.setWeights(chromosome);
        final double fitness = sTestAgent.getFitness();

        mFitnessSum += fitness;
        mNumStepsSum += sTestAgent.getNumSteps();
        ++mNumTests;

        return fitness;
    }

    public double getAverageFitness() {
        return mFitnessSum / mNumTests;
    }

    public double getAverageSteps() {
        return (double) mNumStepsSum / mNumTests;
    }
}
