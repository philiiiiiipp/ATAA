package nl.uva.ataa.evolver;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.environment.BetaPredictor;
import nl.uva.ataa.environment.Predictor;

public class PredictorEvolver {

    private final List<Predictor> mPredictors = new ArrayList<>();

    public PredictorEvolver(final int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            mPredictors.add(new BetaPredictor());
        }
    }

    public void evolvePredictors() {
        // TODO
    }

    public List<Predictor> getPredictors() {
        return mPredictors;
    }

}
