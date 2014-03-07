package nl.uva.ataa.environment;

import nl.uva.ataa.evolver.gene.NeuralNetworkGene;
import nl.uva.ataa.utilities.Utilities;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.jgap.IChromosome;

public class BetaPredictor extends Predictor {

    private final double[] mDistributionWeights = new double[16];

    public BetaPredictor() {
        for (int i = 0; i < mDistributionWeights.length; ++i) {
            mDistributionWeights[i] = Utilities.RNG.nextDouble();
        }
    }

    public BetaPredictor(final IChromosome chromosome) {
        for (int i = 0; i < chromosome.getGenes().length; ++i) {
            final NeuralNetworkGene superGene = (NeuralNetworkGene) chromosome.getGene(i);
            mDistributionWeights[i] = superGene.doubleValue();
        }
    }

    @Override
    protected double getSample(final int index) {
        final int weightIndex = index * 2;
        return getSample(mDistributionWeights[weightIndex], mDistributionWeights[weightIndex + 1]);
    }

    private double getSample(final double alpha, final double beta) {
        final BetaDistribution betaDist = new BetaDistribution(alpha, beta);
        return betaDist.cumulativeProbability(Utilities.RNG.nextDouble());
    }
}
