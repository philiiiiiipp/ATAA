package nl.uva.ataa.evolver.genetic;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * A chromosome that can contain any Double value.
 */
public class DoubleChromosome extends AbstractListChromosome<Double> {

    private final double mFitness;

    public DoubleChromosome(final List<Double> representation, final double fitness)
            throws InvalidRepresentationException {
        super(representation);
        mFitness = fitness;
    }

    public DoubleChromosome(final Double[] representation, final double fitness) throws InvalidRepresentationException {
        this(Arrays.asList(representation), fitness);
    }

    @Override
    public double fitness() {
        return mFitness;
    }

    public double[] getPrimitiveWeights() {
        final List<Double> representation = getRepresentation();
        final double[] weights = new double[representation.size()];

        for (int i = 0; i < weights.length; ++i) {
            weights[i] = representation.get(i);
        }

        return weights;
    }

    public Double[] getWeights() {
        final List<Double> representation = getRepresentation();
        return representation.toArray(new Double[representation.size()]);
    }

    @Override
    public List<Double> getRepresentation() {
        return super.getRepresentation();
    }

    @Override
    public AbstractListChromosome<Double> newFixedLengthChromosome(final List<Double> chromosomeRepresentation) {
        return new DoubleChromosome(chromosomeRepresentation, ChromosomeSpecimen.NO_FITNESS);
    }

    @Override
    protected void checkValidity(final List<Double> chromosomeRepresentation) throws InvalidRepresentationException {
        // Always valid
    }

}
