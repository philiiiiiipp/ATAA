package nl.uva.ataa.agent;

import nl.uva.ataa.agent.neuronfunctions.SigmoidTransferFunction;
import nl.uva.ataa.agent.neuronfunctions.SumInputFunction;
import nl.uva.ataa.agent.neuronfunctions.SumTransferFunction;
import nl.uva.ataa.evolver.genetic.ChromosomeSpecimen;
import nl.uva.ataa.evolver.genetic.DoubleChromosome;
import nl.uva.ataa.evolver.genetic.DoubleMutationPolicy;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;

public class ShimonsAgent extends NeuralNetworkAgent implements ChromosomeSpecimen {

    /** The response for the sigmoid in the transfer function */
    private static final double SIGMOID_RESPONSE = 4.924273;

    /** An input function for neurons that sums the weighted input */
    private static final InputFunction mSumInputFunction = new SumInputFunction();
    /** A transfer function for neurons that puts the input in a sigmoid */
    private static final TransferFunction mSigmoidTransferFunction = new SigmoidTransferFunction(SIGMOID_RESPONSE);
    /** A transfer function for neurons that takes the summed input and returns it without change */
    private static final TransferFunction mSumTransferFunction = new SumTransferFunction();

    public ShimonsAgent() {}

    @Override
    protected void buildNeuralNetwork() {
        // Build the neural network as specified in (Fig 1. Koppejan & Whiteson, 2011)

        setInputLayer(9, mSumInputFunction, mSumTransferFunction);
        addLayerWithBias(3, mSumInputFunction, mSigmoidTransferFunction);
        addLayerWithBias(4, mSumInputFunction, mSumTransferFunction);
        addLayerWithBias(4, mSumInputFunction, mSigmoidTransferFunction);
        setOutputLayerWithBias(4, mSumInputFunction, mSumTransferFunction);

        // Connect input to first hidden layer
        connectNeurons(0, 0, 1, 0);
        connectNeurons(0, 3, 1, 1);
        connectNeurons(0, 7, 1, 2);

        // Connect input and first hidden layer to second hidden layer
        connectNeurons(1, 0, 2, 0);
        connectNeurons(0, 0, 2, 0);
        connectNeurons(0, 1, 2, 0);
        connectNeurons(0, 2, 2, 0);

        connectNeurons(1, 1, 2, 1);
        connectNeurons(0, 3, 2, 1);
        connectNeurons(0, 4, 2, 1);
        connectNeurons(0, 5, 2, 1);

        connectNeurons(0, 6, 2, 2);

        connectNeurons(1, 2, 2, 3);
        connectNeurons(0, 7, 2, 3);
        connectNeurons(0, 8, 2, 3);

        // Connect second hidden layer to third hidden layer
        connectNeurons(2, 0, 3, 0);
        connectNeurons(2, 1, 3, 1);
        connectNeurons(2, 2, 3, 2);
        connectNeurons(2, 3, 3, 3);

        // Connect second and third hidden layer to output
        connectNeurons(3, 0, 4, 0);
        connectNeurons(2, 0, 4, 0);
        connectNeurons(3, 1, 4, 1);
        connectNeurons(2, 1, 4, 1);
        connectNeurons(3, 2, 4, 2);
        connectNeurons(2, 2, 4, 2);
        connectNeurons(3, 3, 4, 3);
        connectNeurons(2, 3, 4, 3);
    }

    @Override
    protected double[] getInput(final double[] o) {
        return new double[] { o[4], o[1], o[9], o[3], o[0], o[10], o[11], o[5], o[2] };
    }

    @Override
    public double getFitness() {
        return getAverageReward();
    }

    @Override
    public Chromosome getChromosome() {
        return new DoubleChromosome(getWeights(), getFitness());
    }

    @Override
    public void setChromosome(final Chromosome chromosome) {
        setWeights(((DoubleChromosome) chromosome).getPrimitiveWeights());
    }

    @Override
    public MutationPolicy getMutationPolicy(final double mutationRange) {
        return new DoubleMutationPolicy(mutationRange);
    }

}
