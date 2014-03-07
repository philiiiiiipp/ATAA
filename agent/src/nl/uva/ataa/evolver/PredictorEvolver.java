package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.environment.Predictor;

public class PredictorEvolver {

    private final List<Predictor> mPredictors = new ArrayList<>();
    private final int mPoolSize;

    public PredictorEvolver(final int poolSize) {
        mPoolSize = poolSize;
        for (int i = 0; i < mPoolSize; i++) {
            mPredictors.add(new Predictor());
        }
    }

    public void evolvePredictors() {
        // TODO
    }

    public List<Predictor> getPredictors() {
        return mPredictors;
    }

}
