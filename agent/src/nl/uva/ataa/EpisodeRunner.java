package nl.uva.ataa;

import java.util.List;
import java.util.Locale;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.environment.BaselineStatePredictor;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.StatePredictor;
import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.environment.fitness.VarianceFitness;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.StatePredictorEvolver;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EpisodeRunner {

    public static final int NUM_PREDICTORS = 30;
    public static final int NUM_AGENTS = 30;
    public static final int ENVIRONMENTS_PER_EVALUATION = 10;
    public static final int NUM_GENERATIONS = 100;
    public static final int EPISODE_LENGTH = 15;

    public static final int NUM_PARAM_VALUES = 4;
    public static final int BASELINE_SIZE = 50000;

    public static final boolean PREDICTOR_EVOLUTION = true;
    public static final boolean CORRECT_BIAS = true;

    private static final FitnessFunction PREDICTOR_FITNESS = new VarianceFitness();

    public static final boolean PRINT_FOR_MATLAB = true;
    public static final boolean PRINT_PREDICTION_ERROR = false;

    public static void main(final String[] args) {

        final BaselineStatePredictor baselinePredictor = new BaselineStatePredictor(NUM_PARAM_VALUES, BASELINE_SIZE,
                PREDICTOR_FITNESS);
        final ShimonsAgent baselineAgent = new ShimonsAgent();
        baselineAgent.setWeights(new double[] { -0.7118, 0.3188, -1.2159, 2.3283, 0.1129, -1.1902, -0.5062, 0.5478,
                -0.7646, -0.2285, -2.5869, -0.225, 0.8412, -1.6608, -0.2988, 2.937, 0.0, 1.3106, -0.2752, 0.2084,
                -2.3259, -0.3596, 0.3116, 0.773, 0.7982, -0.5549, -0.0631, 2.0086, -0.6239, -1.346, 0.2472, 1.4733,
                1.3579, 0.3694, 0.0288, 1.808, 0.4698, -1.0696, -0.178, 0.0742, -0.0004, -1.105 });

        final AgentEvolver agentEvolver = new AgentEvolver(NUM_AGENTS);
        agentEvolver.setBiasCorrection(CORRECT_BIAS);
        final List<ShimonsAgent> agents = agentEvolver.getSpecimens();

        final StatePredictorEvolver predictorEvolver = new StatePredictorEvolver(NUM_PREDICTORS, PREDICTOR_FITNESS,
                NUM_PARAM_VALUES);
        final List<StatePredictor> predictors = predictorEvolver.getSpecimens();

        for (int generation = 0; generation < NUM_GENERATIONS; ++generation) {
            // Performs tests for each agent and each predictor
            for (final ShimonsAgent agent : agents) {
                for (final Predictor predictor : predictors) {
                    for (int i = 0; i < ENVIRONMENTS_PER_EVALUATION; ++i) {
                        runEpisode(predictor, agent);
                    }
                }
            }

            // Test the best agent against the baseline
            final ShimonsAgent bestAgent = agentEvolver.cloneBestAgent();
            for (int i = 0; i < baselinePredictor.getTotalNumberOfBaselines(); ++i) {
                runEpisode(baselinePredictor, bestAgent);
            }

            // Test the generic/baseline agent from Shimon to see how good the true value is predicted
            if (PRINT_PREDICTION_ERROR) {
                for (final Predictor predictor : predictors) {
                    for (int i = 0; i < ENVIRONMENTS_PER_EVALUATION; ++i) {
                        runEpisode(predictor, baselineAgent);
                    }
                }

                final double avgRewardBaselineTrue = -24370.5291489543;
                final double avgRewardBaselinePred = baselineAgent.getAverageReward();
                final double avgRewardBaselineErr = Math.abs(avgRewardBaselineTrue - avgRewardBaselinePred);
                System.out.println("Error in predicting agent fitness: " + avgRewardBaselineErr);
            }

            // Print scores
            final long agentFitness = agentEvolver.getAverageFitness();
            final String steps = formatSteps(agentEvolver.getAverageNumSteps());
            final long predictorFitness = predictorEvolver.getAverageFitness();
            final long baselineAgentFitness = Math.round(bestAgent.getAverageReward());
            final String baselineSteps = formatSteps(bestAgent.getAverageNumSteps());

            if (PRINT_FOR_MATLAB) {
                System.out.println(agentFitness + " " + steps + " " + predictorFitness + " " + baselineAgentFitness
                        + " " + baselineSteps + ";");
            } else {
                System.out.println(agentFitness + "  --  " + steps + "  --  " + predictorFitness + "          "
                        + baselineAgentFitness + "  --  " + baselineSteps);
            }

            // Evolve the specimens
            agentEvolver.evolve();
            if (PREDICTOR_EVOLUTION) {
                predictorEvolver.evolve();
            } else {
                predictorEvolver.refill();
            }

            // Clean up the specimens
            for (final ShimonsAgent agent : agents) {
                agent.agent_cleanup();
            }
            for (final Predictor predictor : predictors) {
                predictor.env_cleanup();
            }
            baselinePredictor.env_cleanup();
        }
    }

    /**
     * Perform one glue step with a given predictor and agent.
     * 
     * @param predictor
     *            The given predictor
     * @param agent
     *            The given agent
     */
    private static void runEpisode(final Predictor predictor, final AgentInterface agent) {
        agent.agent_init(predictor.env_init());

        // Perform the first step in the episode
        final Observation initObs = predictor.env_start();
        Action action = agent.agent_start(initObs);
        Reward_observation_terminal rewObs = null;

        // Perform steps until the episode is over or the state is terminal
        for (int timestep = 0; timestep < EPISODE_LENGTH; timestep++) {
            rewObs = predictor.env_step(action);
            if (rewObs.isTerminal()) {
                break;
            }
            action = agent.agent_step(rewObs.getReward(), rewObs.getObservation());
        }
        agent.agent_end(rewObs.getReward());
    }

    private static String formatSteps(final double numSteps) {
        return String.format(Locale.ENGLISH, "%.2f", numSteps);
    }
}
