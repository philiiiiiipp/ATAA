package nl.uva.ataa.agent;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IGeneConstraintChecker;
import org.jgap.InvalidConfigurationException;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class NeuroEvolutionaryAgent extends NeuralNetworkAgent {

    /** The response for the sigmoid in the transfer function */
    private static final double SIGMOID_RESPONSE = 4.924273;

    /** An input function for neurons that sums the weighted input */
    private static final InputFunction mSumInputFunction = new SumInputFunction();
    /** A transfer function for neurons that puts the input in a sigmoid */
    private static final TransferFunction mSigmoidTransferFunction = new SigmoidTransferFunction(SIGMOID_RESPONSE);
    /** A transfer function for neurons that takes the summed input and returns it without change */
    private static final TransferFunction mSumTransferFunction = new SumTransferFunction();

    @Override
    protected void buildNeuralNetwork() {
        // Build the neural network as specified in (Fig 1. Koppejan & Whiteson, 2011)

        addLayer(9, mSumInputFunction, mSumTransferFunction);
        addLayerWithBias(3, mSumInputFunction, mSigmoidTransferFunction);
        addLayerWithBias(4, mSumInputFunction, mSumTransferFunction);
        addLayerWithBias(4, mSumInputFunction, mSigmoidTransferFunction);
        addLayerWithBias(4, mSumInputFunction, mSumTransferFunction);

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
        return new double[] { o[3], o[0], o[10], o[4], o[1], o[9], o[11], o[5], o[2] };
    }

    public static void main(final String[] args) {
        new AgentLoader(new NeuroEvolutionaryAgent()).run();
    }

	@Override
	public Gene getGene(int a_desiredLocus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gene[] getGenes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenes(Gene[] a_genes) throws InvalidConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFitnessValue(double a_newFitnessValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFitnessValueDirectly(double a_newFitnessValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getFitnessValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getFitnessValueDirectly() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIsSelectedForNextGeneration(boolean a_isSelected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSelectedForNextGeneration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConstraintChecker(IGeneConstraintChecker a_constraintChecker)
			throws InvalidConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationData(Object a_newData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getApplicationData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increaseAge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAge(int a_age) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void increaseOperatedOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetOperatedOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int operatedOn() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUniqueID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUniqueIDTemplate(String a_templateID, int a_index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUniqueIDTemplate(int a_index) {
		// TODO Auto-generated method stub
		return null;
	}

}
