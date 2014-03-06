package nl.uva.ataa.environment;

import nl.uva.ataa.agent.genetic.gene.WindGene;

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

    public Wind(final WindGene[] windGenes) {
        mMaxStr = windGenes[0].doubleValue();
        mHz = windGenes[1].doubleValue();
        mPhase = windGenes[2].doubleValue();
        mCenterAmp = windGenes[3].doubleValue();
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
