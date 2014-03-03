package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;
import nl.uva.ataa.agent.NeuralNetworkAgent;
import nl.uva.ataa.agent.NeuroEvolutionaryAgent;

public class AgentEvolver {

    private final List<NeuralNetworkAgent> mAgents = new ArrayList<>();

    private final int mPoolSize;

    public AgentEvolver(final int poolSize) {
        mPoolSize = poolSize;
        for (int i = 0; i < mPoolSize; i++) {
        }
    }

    public void evolveAgents() {
        // TODO
    }

    public List<NeuralNetworkAgent> getAgents() {
        return mAgents;
    }

}
