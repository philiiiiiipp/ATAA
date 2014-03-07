package nl.uva.ataa.agent.neuronfunctions;

import org.neuroph.core.transfer.TransferFunction;

public class SumTransferFunction extends TransferFunction {

    private static final long serialVersionUID = 74543465670027718L;

    @Override
    public double getOutput(final double net) {
        // The next should already be summed in the input function
        return net;
    }

}
