package nl.uva.ataa.evolver.genetic;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

public class DoubleLimitMutationPolicy implements MutationPolicy {

    /** The range that a gene can maximaly mutate */
    final double mMutationRange;

    /**
     * Creates a new mutation policy.
     * 
     * @param mutationRange
     *            The range that a gene can maximaly mutate
     */
    public DoubleLimitMutationPolicy(final double mutationRange) {
        mMutationRange = mutationRange;
    }

    @Override
    public Chromosome mutate(final Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof DoubleLimitChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES,
                    original.getClass().getSimpleName());
        }

        final DoubleLimitChromosome originalDc = (DoubleLimitChromosome) original;
        final List<Double> repr = originalDc.getRepresentation();
        final int i = Utilities.RNG.nextInt(repr.size());

        final List<Double> newRepr = new ArrayList<Double>(repr);
        double newValue;
        do {
            newValue = repr.get(i) + Utilities.RNG.nextDouble() * mMutationRange - mMutationRange / 2;
        } while (!originalDc.isValid(newValue));
        newRepr.set(i, newValue);

        return originalDc.newFixedLengthChromosome(newRepr);
    }
}
