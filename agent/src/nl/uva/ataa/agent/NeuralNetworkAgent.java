package nl.uva.ataa.agent;

import java.util.List;

import nl.uva.ataa.EpisodeRunner;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.WindEnvironment;
import nl.uva.ataa.evolver.gene.NeuralNetworkGene;
import nl.uva.ataa.utilities.Utilities;

import org.jgap.IChromosome;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public abstract class NeuralNetworkAgent implements AgentInterface {

    /** The neural network used to map observations to actions */
    protected final NeuralNetwork<?> mNeuralNetwork = new NeuralNetwork<>();

    protected List<Predictor> mPredictors;

    /** The nr of episodes run during tests */
    private int mNrEpisodes = 0;

    private int mNumSteps = 0;

    /** The rewards that the agent has gathered during tests */
    private double mAccumulatedReward = 0.0;

    public NeuralNetworkAgent() {
        buildNeuralNetwork();
    }

    /**
     * Builds the neural network structure to map observations to actions.
     */
    protected abstract void buildNeuralNetwork();

    /**
     * Adds a layer to the neural network.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected void addLayer(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        mNeuralNetwork.addLayer(getLayer(numNeurons, inputFunction, transferFunction));
    }

    /**
     * Adds a layer to the neural network and adds a bias neuron which connects to every neuron in the new layer.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected void addLayerWithBias(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        mNeuralNetwork.addLayer(getLayerWithBias(numNeurons, inputFunction, transferFunction));
    }

    /**
     * Sets the input layer for this neural network.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected void setInputLayer(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        final Layer layer = getLayer(numNeurons, inputFunction, transferFunction);
        mNeuralNetwork.setInputNeurons(layer.getNeurons());
        mNeuralNetwork.addLayer(0, layer);
    }

    /**
     * Sets the output layer for this neural network and adds bias to it.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected void setOutputLayerWithBias(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        final Layer layer = getLayerWithBias(numNeurons, inputFunction, transferFunction);
        mNeuralNetwork.setOutputNeurons(layer.getNeurons());
        mNeuralNetwork.addLayer(layer);
    }

    /**
     * Creates a layer containing neurons.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected Layer getLayer(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        final Layer layer = new Layer();
        for (int i = 0; i < numNeurons; ++i) {
            layer.addNeuron(new Neuron(inputFunction, transferFunction));
        }
        return layer;
    }

    /**
     * Creates a layer with a bias neuron which connects to every neuron in the layer.
     * 
     * @param numNeurons
     *            The amount of neurons in the new layer
     * @param inputFunction
     *            The input function to use for the neurons in the new layer
     * @param transferFunction
     *            The transfer function to use for the neurons in the new layer
     */
    protected Layer getLayerWithBias(final int numNeurons, final InputFunction inputFunction,
            final TransferFunction transferFunction) {
        final Neuron bias = new BiasNeuron();

        final Layer layer = getLayer(numNeurons, inputFunction, transferFunction);
        for (final Neuron neuron : layer.getNeurons()) {
            neuron.addInputConnection(bias);
        }

        return layer;
    }

    /**
     * Connects two neurons to eachother.
     * 
     * @param inputLayer
     *            The index of the layer container the input neuron
     * @param inputNeuron
     *            The index of the input neuron within the given layer
     * @param outputLayer
     *            The index of the layer container the output neuron
     * @param outputNeuron
     *            The index of the output neuron within the given layer
     */
    protected void connectNeurons(final int inputLayer, final int inputNeuron, final int outputLayer,
            final int outputNeuron) {
        getNeuron(outputLayer, outputNeuron).addInputConnection(getNeuron(inputLayer, inputNeuron));
    }

    /**
     * Retrieves the neuron at the specified coordinates.
     * 
     * @param layer
     *            The layer of the neuron
     * @param neuron
     *            The index of the neuron within the layer
     * 
     * @return The neuron
     */
    private Neuron getNeuron(final int layer, final int neuron) {
        return mNeuralNetwork.getLayerAt(layer).getNeuronAt(neuron);
    }

    /**
     * Sets all the weights in the neural network.
     * 
     * @param weights
     *            The weights ordered by connection
     */
    public void setWeights(final double[] weights) {
        mNeuralNetwork.setWeights(weights);
    }

    /**
     * Sets all the weights in the neural network.
     * 
     * @param weights
     *            The weights ordered by connection
     */
    public void setWeights(final IChromosome chromosome) {
        final double[] weights = new double[chromosome.getGenes().length];

        for (int i = 0; i < chromosome.getGenes().length; ++i) {
            final NeuralNetworkGene superGene = (NeuralNetworkGene) chromosome.getGene(i);
            weights[i] = superGene.doubleValue();
        }

        setWeights(weights);
    }

    /**
     * Retrieves all the weights in the neural network.
     * 
     * @return The weights ordered by connection
     */
    public Double[] getWeights() {
        return mNeuralNetwork.getWeights();
    }

    /**
     * Retrieves the input for the neural network based on an observation.
     * 
     * @param observation
     *            The observation given by the environment
     * 
     * @return The input for the neural network
     */
    protected abstract double[] getInput(final double[] observation);

    /**
     * Uses the neural network to calculate the action given an observation.
     * 
     * @param observation
     *            The observation given by the environment
     * 
     * @return The action to perform
     */
    private Action getAction(final Observation o) {
        mNeuralNetwork.setInput(getInput(o.doubleArray));
        mNeuralNetwork.calculate();

        final Action action = new Action();
        action.doubleArray = mNeuralNetwork.getOutput();
        // System.out.println("A = " + Arrays.toString(action.doubleArray));
        return action;
    }

    /**
     * Returns the fitness of the agent based on the last series of tests it has performed.
     * 
     * @return The agent's fitness
     */
    public double getFitness() {
        for (int i = 0; i < EpisodeRunner.ENVS_PER_EVALUATION; ++i) {
            final Predictor predictor = mPredictors.get(Utilities.RNG.nextInt(mPredictors.size()));
            final WindEnvironment environment = predictor.generateEnvironment();

            agent_init(environment.env_init());

            EpisodeRunner.run(environment, this);

            mAccumulatedReward -= EpisodeRunner.MIN_REWARD;
        }
        final double fitness = mAccumulatedReward / mNrEpisodes;

        // System.out.println("STEPS: " + ((int) (((double) mNrSteps / mNrEpisodes) * 100)) / 100.0);
        // System.out.println("FITNS: " + fitness);

        agent_cleanup();

        return fitness;
    }

    public int getNumSteps() {
        return mNumSteps;
    }

    @Override
    public void agent_init(final String taskSpec) {
        mAccumulatedReward = 0.0;
        mNrEpisodes = 0;
        mNumSteps = 0;
    }

    @Override
    public String agent_message(final String arg0) {
        return null;
    }

    @Override
    public Action agent_start(final Observation o) {
        ++mNumSteps;
        return getAction(o);
    }

    @Override
    public Action agent_step(final double reward, final Observation o) {
        ++mNumSteps;
        mAccumulatedReward += reward;
        return getAction(o);
    }

    @Override
    public void agent_end(final double reward) {
        mAccumulatedReward += reward;
        ++mNrEpisodes;
    }

    @Override
    public void agent_cleanup() {}

    public void setPredictors(final List<Predictor> predictors) {
        mPredictors = predictors;
    }

}
