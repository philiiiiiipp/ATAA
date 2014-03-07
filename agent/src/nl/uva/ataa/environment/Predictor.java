package nl.uva.ataa.environment;

public abstract class Predictor {

    /**
     * Generates a random environment, based on the implementation's distribution.
     * 
     * @return A random environment
     */
    public WindEnvironment generateEnvironment() {
        final Wind windNS = new Wind(getSample(0), getSample(1), getSample(2), getSample(3));
        final Wind windEW = new Wind(getSample(4), getSample(5), getSample(6), getSample(7));
        return new WindEnvironment(windNS, windEW);
    }

    /**
     * Must return a sample in [0, 1] to be used as a wind parameter.
     * 
     * @param index
     *            The index of the sample, ranging from 0 to 7, corresponding to the the two winds' parameters
     * @return A sample in [0, 1]
     */
    protected abstract double getSample(final int index);
}
