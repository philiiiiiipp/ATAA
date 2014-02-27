package nl.uva.ataa.agent;

import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class NeuroEvolutionaryAgent implements AgentInterface {

    private static final int DIM_INPUT = 12;
    private static final int DIM_HIDDEN = 11;
    private static final int DIM_OUTPUT = 4;
    private static final double SIGMOID_RESPONSE = 4.924273;

    private static final InputFunction mSumInputFunction = new SumInputFunction();
    private static final TransferFunction mSigmoidTransferFunction = new SigmoidTransferFunction(SIGMOID_RESPONSE);
    private static final TransferFunction mSumTransferFunction = new SumTransferFunction();

    private final MultiLayerPerceptron mNeuralNetwork;

    private Action action;

    TaskSpec TSO = null;

    public NeuroEvolutionaryAgent() {
        mNeuralNetwork = new MultiLayerPerceptron(DIM_INPUT, DIM_HIDDEN, DIM_OUTPUT);
        System.out.println(mNeuralNetwork.getLayersCount());
    }

    @Override
    public void agent_cleanup() {}

    @Override
    public void agent_end(final double arg0) {

    }

    public void agent_freeze() {

    }

    @Override
    public void agent_init(final String taskSpec) {
        TSO = new TaskSpec(taskSpec);
        action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
    }

    @Override
    public String agent_message(final String arg0) {
        return null;
    }

    @Override
    public Action agent_start(final Observation o) {
        return action;
    }

    @Override
    public Action agent_step(final double arg0, final Observation o) {
        return action;
    }

    public static void main(final String[] args) {
        final AgentLoader L = new AgentLoader(new NeuroEvolutionaryAgent());
        L.run();
    }

}
