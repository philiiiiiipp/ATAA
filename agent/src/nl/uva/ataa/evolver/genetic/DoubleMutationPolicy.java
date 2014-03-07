package nl.uva.ataa.evolver.genetic;

import java.util.ArrayList;
import java.util.List;

import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

public class DoubleMutationPolicy implements MutationPolicy {

    /** The range that a gene can maximaly mutate */
    final double mMutationRange;

    /**
     * Creates a new mutation policy.
     * 
     * @param mutationRange
     *            The range that a gene can maximaly mutate
     */
    public DoubleMutationPolicy(final double mutationRange) {
        mMutationRange = mutationRange;
    }

    @Override
    public Chromosome mutate(final Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof DoubleChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES,
                    original.getClass().getSimpleName());
        }

        final DoubleChromosome originalDc = (DoubleChromosome) original;
        final List<Double> repr = originalDc.getRepresentation();
        final int i = Utilities.RNG.nextInt(repr.size());

        final List<Double> newRepr = new ArrayList<Double>(repr);
        newRepr.set(i, repr.get(i) + Utilities.RNG.nextDouble() * mMutationRange - mMutationRange / 2);

        return originalDc.newFixedLengthChromosome(newRepr);
    }
}
