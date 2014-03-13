package nl.uva.ataa;

import java.util.List;
import java.util.Locale;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.environment.BaselinePredictor;
import nl.uva.ataa.environment.DiscreteWindPredictor;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.environment.fitness.RewardFitness;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.DiscreteWindPredictorEvolver;

import org.rlcommunity.environment.helicopter.HelicopterState;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EpisodeRunner {

    /** The amount of predictors to evolve */
    private static final int NUM_PREDICTORS = 1;
    /** The amount of agents to evolve */
    private static final int NUM_AGENTS = 50;

    /** The amount of environments tested per agent per generation */
    private static final int ENVIRONMENTS_PER_EVALUATION = 50;
    /** The amount of generations to evolve */
    private static final int NUM_GENERATIONS = 100;
    /** The maximum length of an episode */
    public static final int EPISODE_LENGTH = 1000;

    /** The fitness function to test the predictor with */
    private static final FitnessFunction PREDICTOR_FITNESS = new RewardFitness();

    /** The minimum possible reward after an episode */
    public static final double MIN_REWARD = (-3.0f * HelicopterState.MAX_POS * HelicopterState.MAX_POS + -3.0f
            * HelicopterState.MAX_RATE * HelicopterState.MAX_RATE + -3.0f * HelicopterState.MAX_VEL
            * HelicopterState.MAX_VEL - (1.0f - HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE
            * HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE))
            * EpisodeRunner.EPISODE_LENGTH;

    public static void main(final String[] args) {

        final DiscreteWindPredictorEvolver predictorEvolver = new DiscreteWindPredictorEvolver(NUM_PREDICTORS,
                PREDICTOR_FITNESS);
        final AgentEvolver agentEvolver = new AgentEvolver(NUM_AGENTS);

        final List<DiscreteWindPredictor> predictors = predictorEvolver.getSpecimens();
        final List<ShimonsAgent> agents = agentEvolver.getSpecimens();
        final List<BaselinePredictor> baselinePredictors = BaselinePredictor.getAllBaselines();

        final long lastBaselineReward = 0;
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {

            // final long averageBaselineReward = runBaselineTest(baselinePredictors, agents);
            // System.out.println("Baseline average reward over " + baselinePredictors.size() + " environments: "
            // + averageBaselineReward + " diff: " + (averageBaselineReward - lastBaselineReward));
            // lastBaselineReward = averageBaselineReward;

            // Performs tests for each agent and each predictor
            for (final ShimonsAgent agent : agents) {
                for (final Predictor predictor : predictors) {
                    for (int i = 0; i < EpisodeRunner.ENVIRONMENTS_PER_EVALUATION; ++i) {
                        runEpisode(predictor, agent);
                    }
                }
            }

            // Print scores
            System.out.println(Math.round(agentEvolver.getAverageFitness()) + " - "
                    + String.format(Locale.ENGLISH, "%.2f", agentEvolver.getAverageNumSteps()) + "     -----     "
                    + Math.round(predictorEvolver.getAverageFitness()));

            // Evolve the specimens
            // predictorEvolver.evolve();
            agentEvolver.evolve();

            System.out.print("                                        ");
            final double[] distributionWeights = new double[10];
            for (final DiscreteWindPredictor predictor : predictors) {
                for (int i = 0; i < 10; ++i) {
                    distributionWeights[i] += predictor.mDistributionWeights[i];
                }
            }
            for (int i = 0; i < 10; ++i) {
                System.out.print(Math.round(distributionWeights[i] * 10) / (10.0 * NUM_PREDICTORS) + "  ");
            }
            System.out.println();

            // Clean up the specimens
            for (final Predictor predictor : predictors) {
                predictor.env_cleanup();
            }
            for (final ShimonsAgent agent : agents) {
                agent.agent_cleanup();
            }
        }

    }

    /**
     * Run the baseline test for the given predictors and agents
     * 
     * @param baselinePredictors
     *            The baseline predictors
     * @param agents
     *            The agents
     * @return The average rewards over all agents and baselines
     */
    private static long runBaselineTest(final List<BaselinePredictor> baselinePredictors,
            final List<ShimonsAgent> agents) {

        // Performs tests for each agent
        for (final ShimonsAgent agent : agents) {
            for (final BaselinePredictor predictor : baselinePredictors) {
                for (int i = 0; i < EpisodeRunner.ENVIRONMENTS_PER_EVALUATION; ++i) {
                    runEpisode(predictor, agent);
                }
            }
        }

        double totalReward = 0;
        for (final ShimonsAgent agent : agents) {
            totalReward += agent.getAverageReward();
        }

        // Clean up the specimens
        for (final Predictor predictor : baselinePredictors) {
            predictor.env_cleanup();
        }
        for (final ShimonsAgent agent : agents) {
            agent.agent_cleanup();
        }

        return Math.round(totalReward / agents.size());
    }

    /**
     * Perform one glue step with a given predictor and agent
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
}
