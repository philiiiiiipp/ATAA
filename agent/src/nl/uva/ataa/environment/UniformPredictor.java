package nl.uva.ataa.environment;

import nl.uva.ataa.utilities.Utilities;

public class UniformPredictor extends Predictor {

    @Override
    protected double getSample(final int index) {
        return Utilities.RNG.nextDouble();
    }
}
