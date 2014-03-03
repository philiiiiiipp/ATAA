package nl.uva.glue;

import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.EnvironmentEvolver;

import org.rlcommunity.environment.helicopter.Helicopter;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class Main {

    public static void main(final String[] args) {
        EnvironmentEvolver environmentEvolver = new EnvironmentEvolver(5);
        AgentEvolver agentEvolver = new AgentEvolver(5);

        final int episodeLength = 1000;

        for (NeuralNetworkAgent agent : agentEvolver.getAgents()) {
            for (Helicopter environment : environmentEvolver.getEnvironments()) {
                final Observation initObs = environment.env_start();
                Action action = agent.agent_start(initObs);
                Reward_observation_terminal rewObs = null;
                for (int i = 0; i < episodeLength; i++) {
                    rewObs = environment.env_step(action);
                    if (rewObs.isTerminal()) {
                        break;
                    }
                    action = agent.agent_step(rewObs.getReward(), rewObs.getObservation());
                }
                agent.agent_end(rewObs.getReward());
            }
        }
    }
}
