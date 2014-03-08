package nl.uva.ataa.evolver.genetic;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * A chromosome that can contain a Double value within the limits.
 */
public class DoubleLimitChromosome extends DoubleChromosome {

    private boolean mInitialised = false;
    private final double mLowerBound;
    private final double mUpperBound;
    private final boolean mLowerInclusive;
    private final boolean mUpperInclusive;

    public DoubleLimitChromosome(final List<Double> representation, final double fitness, final double lowerBound,
            final double upperBound, final boolean lowerInclusive, final boolean upperInclusive)
            throws InvalidRepresentationException {
        super(representation, fitness);
        mLowerBound = lowerBound;
        mUpperBound = upperBound;
        mLowerInclusive = lowerInclusive;
        mUpperInclusive = upperInclusive;
        finishInitialisation();
    }

    public DoubleLimitChromosome(final Double[] representation, final double fitness, final double lowerBound,
            final double upperBound, final boolean lowerInclusive, final boolean upperInclusive)
            throws InvalidRepresentationException {
        this(Arrays.asList(representation), fitness, lowerBound, upperBound, lowerInclusive, upperInclusive);
    }

    private void finishInitialisation() throws InvalidRepresentationException {
        mInitialised = true;
        checkValidity(getRepresentation());
    }

    public double getLowerBound() {
        return mLowerBound;
    }

    public double getUpperBound() {
        return mUpperBound;
    }

    @Override
    public AbstractListChromosome<Double> newFixedLengthChromosome(final List<Double> chromosomeRepresentation) {
        return new DoubleLimitChromosome(chromosomeRepresentation, ChromosomeSpecimen.NO_FITNESS, mLowerBound,
                mUpperBound, mLowerInclusive, mUpperInclusive);
    }

    @Override
    protected void checkValidity(final List<Double> chromosomeRepresentation) throws InvalidRepresentationException {
        if (!mInitialised) {
            return;
        }

        for (final Double value : chromosomeRepresentation) {
            if (!isValid(value)) {
                throw new InvalidRepresentationException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, value,
                        mLowerBound, mUpperBound);
            }
        }
    }

    /**
     * Checks if a value is within the bounds of the chromosome.
     * 
     * @param value
     *            A potential gene value
     * 
     * @return True iff the value is acceptable for a gene
     */
    public boolean isValid(final double value) {
        if (mLowerInclusive ? mLowerBound > value : mLowerBound >= value) {
            return false;
        }
        if (mUpperInclusive ? mUpperBound < value : mUpperBound <= value) {
            return false;
        }

        return true;
    }

}
