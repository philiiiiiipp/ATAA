package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.environment.EvolutionaryEnvironment;

import org.rlcommunity.environment.helicopter.Helicopter;

public class EnvironmentEvolver {

    private final List<Helicopter> mEnvironments = new ArrayList<>();
    private final int mPoolSize;

    public EnvironmentEvolver(final int poolSize) {
        mPoolSize = poolSize;
        for (int i = 0; i < mPoolSize; i++) {
            mEnvironments.add(new EvolutionaryEnvironment());
        }
    }

    public void evolveEnvironments(List<Double> rewards) {
        // TODO
    }

    public List<Helicopter> getEnvironments() {
        return mEnvironments;
    }

}
