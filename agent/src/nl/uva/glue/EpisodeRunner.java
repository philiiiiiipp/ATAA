package nl.uva.glue;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EpisodeRunner {

    private static final int EPISODE_LENGTH = 1000;

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
