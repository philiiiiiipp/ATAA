package nl.uva.ataa;

import java.util.List;
import java.util.Locale;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.StatePredictor;
import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.environment.fitness.VarianceFitness;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.StatePredictorEvolver;

import org.rlcommunity.environment.helicopter.HelicopterState;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EpisodeRunner {

    /** The amount of predictors to evolve */
    private static final int NUM_PREDICTORS = 20;
    /** The amount of agents to evolve */
    private static final int NUM_AGENTS = 20;

    /** The amount of environments tested per agent per generation */
    private static final int ENVIRONMENTS_PER_EVALUATION = 10;
    /** The amount of generations to evolve */
    private static final int NUM_GENERATIONS = 100;
    /** The maximum length of an episode */
    public static final int EPISODE_LENGTH = 15;

    public static final int NUM_DISCRETE_VALUES = 4;

    /** The fitness function to test the predictor with */
    private static final FitnessFunction PREDICTOR_FITNESS = new VarianceFitness();

    /** The minimum possible reward after an episode */
    public static final double MIN_REWARD = (-3.0f * HelicopterState.MAX_POS * HelicopterState.MAX_POS + -3.0f
            * HelicopterState.MAX_RATE * HelicopterState.MAX_RATE + -3.0f * HelicopterState.MAX_VEL
            * HelicopterState.MAX_VEL - (1.0f - HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE
            * HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE))
            * EpisodeRunner.EPISODE_LENGTH;

    public static void main(final String[] args) {

        final StatePredictor baselinePredictor = new StatePredictor(NUM_DISCRETE_VALUES, PREDICTOR_FITNESS);

        final AgentEvolver agentEvolver = new AgentEvolver(NUM_AGENTS);
        final StatePredictorEvolver predictorEvolver = new StatePredictorEvolver(NUM_PREDICTORS, PREDICTOR_FITNESS,
                NUM_DISCRETE_VALUES);

        final List<ShimonsAgent> agents = agentEvolver.getSpecimens();
        final List<StatePredictor> predictors = predictorEvolver.getSpecimens();

        for (int generation = 0; generation < NUM_GENERATIONS; ++generation) {
            // Performs tests for each agent and each predictor
            for (final ShimonsAgent agent : agents) {
                for (final Predictor predictor : predictors) {
                    for (int i = 0; i < EpisodeRunner.ENVIRONMENTS_PER_EVALUATION; ++i) {
                        runEpisode(predictor, agent);
                    }
                }
            }

            // Print scores
            System.out.print(Math.round(agentEvolver.getAverageFitness()) + " - "
                    + String.format(Locale.ENGLISH, "%.2f", agentEvolver.getAverageNumSteps()) + "     -----     "
                    + Math.round(predictorEvolver.getAverageFitness()));

            // Test the best agent against the baseline
            final ShimonsAgent bestAgent = agentEvolver.cloneBestAgent();
            for (int i = 0; i < 100; ++i) {
                // TODO: Change stop condition to predictor length
                runEpisode(baselinePredictor, bestAgent);
            }
            System.out.println("          " + Math.round(bestAgent.getAverageReward()) + " - "
                    + String.format(Locale.ENGLISH, "%.2f", bestAgent.getAverageNumSteps()));
            baselinePredictor.env_cleanup();

            // Evolve the specimens
            agentEvolver.evolve();
            predictorEvolver.evolve();

            // Clean up the specimens
            for (final ShimonsAgent agent : agents) {
                agent.agent_cleanup();
            }
            for (final Predictor predictor : predictors) {
                predictor.env_cleanup();
            }
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
}
