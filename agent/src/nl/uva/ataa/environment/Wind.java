package nl.uva.ataa.environment;

public class Wind {

    /** [0, 1], maximum force wind will exert */
    private final double mMaxStr;

    /** [0, 1], number of cycles per second */
    private final double mHz;

    /** [0, 1], a fraction of the wave period */
    private final double mPhase; //

    /** [0, 1], center amplitude of the sine wave */
    private final double mCenterAmp;

    public Wind(final double maxStr, final double hz, final double phase, final double centerAmp) {
        mMaxStr = maxStr;
        mHz = hz;
        mPhase = phase;
        mCenterAmp = centerAmp;
    }

    public double getMaxStr() {
        return mMaxStr;
    }

    public double getHz() {
        return mHz;
    }

    public double getPhase() {
        return mPhase;
    }

    public double getCenterAmp() {
        return mCenterAmp;
    }

    public double[] getParamaterArray() {
        return new double[] { mMaxStr, mHz, mPhase, mCenterAmp };
    }

}
