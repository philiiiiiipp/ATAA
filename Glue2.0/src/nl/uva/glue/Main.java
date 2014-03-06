package nl.uva.glue;

import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.environment.EvolutionaryEnvironment;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.EnvironmentEvolver;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class Main {

    private static final int NUM_GENERATIONS = 10;
    private static final int EPISODE_LENGTH = 1000;

    public static void main(final String[] args) {

        final EnvironmentEvolver environmentEvolver = new EnvironmentEvolver(5);
        final AgentEvolver agentEvolver = new AgentEvolver(5);

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            // run each agent in each environment
            for (final NeuralNetworkAgent agent : agentEvolver.getAgents()) {
                for (final EvolutionaryEnvironment environment : environmentEvolver.getEnvironments()) {
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

            // EVOLVE
            environmentEvolver.evolveEnvironments();
            agentEvolver.evolveAgents();

            // cleanup
            for (final EvolutionaryEnvironment environment : environmentEvolver.getEnvironments()) {
                environment.env_cleanup();
            }
            for (final NeuralNetworkAgent agent : agentEvolver.getAgents()) {
                agent.agent_cleanup();
            }
        }
    }
}
