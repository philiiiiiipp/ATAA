package nl.uva.glue;

import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.environment.EvolutionaryEnvironment;
import nl.uva.ataa.evolver.AgentEvolver;
import nl.uva.ataa.evolver.EnvironmentEvolver;

public class Main {

    private static final int NUM_GENERATIONS = 10;

    public static void main(final String[] args) {

        final EnvironmentEvolver environmentEvolver = new EnvironmentEvolver(5);
        final AgentEvolver agentEvolver = new AgentEvolver(5);

        for (final NeuralNetworkAgent agent : agentEvolver.getAgents()) {
            agent.setEnvironments(environmentEvolver.getEnvironments());
        }

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {

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
