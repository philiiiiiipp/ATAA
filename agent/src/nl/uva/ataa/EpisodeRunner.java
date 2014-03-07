package nl.uva.ataa;

import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.PredictorEvolver;

import org.rlcommunity.environment.helicopter.HelicopterState;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EpisodeRunner {

    public static final int EPISODE_LENGTH = 1000;
    public static final int ENVS_PER_EVALUATION = 10;
    private static final int NUM_GENERATIONS = 10000;

    public static final double MIN_REWARD = (-3.0f * HelicopterState.MAX_POS * HelicopterState.MAX_POS + -3.0f
            * HelicopterState.MAX_RATE * HelicopterState.MAX_RATE + -3.0f * HelicopterState.MAX_VEL
            * HelicopterState.MAX_VEL - (1.0f - HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE
            * HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE))
            * EpisodeRunner.EPISODE_LENGTH;

    public static void main(final String[] args) {

        final PredictorEvolver predictorEvolver = new PredictorEvolver(1);
        final AgentEvolver agentEvolver = new AgentEvolver(50);

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            agentEvolver.evolveAgents(predictorEvolver.getPredictors());
        }
    }

    public static void run(final EnvironmentInterface environment, final AgentInterface agent) {
        environment.env_start();
        agent.agent_init("");

        final Observation initObs = environment.env_start();
        Action action = agent.agent_start(initObs);
        Reward_observation_terminal rewObs = null;

        for (int timestep = 0; timestep < EPISODE_LENGTH; timestep++) {
            rewObs = environment.env_step(action);
            if (rewObs.isTerminal()) {
                break;
            }
            action = agent.agent_step(rewObs.getReward(), rewObs.getObservation());
        }
        agent.agent_end(rewObs.getReward());
    }
}
