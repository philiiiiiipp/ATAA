package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.environment.EvolutionaryEnvironment;

public class EnvironmentEvolver {

	private final List<EvolutionaryEnvironment> mEnvironments = new ArrayList<>();
	private final int mPoolSize;

	public EnvironmentEvolver(final int poolSize) {
		mPoolSize = poolSize;
		for (int i = 0; i < mPoolSize; i++) {
			mEnvironments.add(new EvolutionaryEnvironment());
		}
	}

	public void evolveEnvironments() {
		// TODO
	}

	public List<EvolutionaryEnvironment> getEnvironments() {
		return mEnvironments;
	}

}
