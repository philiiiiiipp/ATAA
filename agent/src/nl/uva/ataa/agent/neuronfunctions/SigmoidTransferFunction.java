package nl.uva.ataa.agent.neuronfunctions;

import org.neuroph.core.transfer.TransferFunction;

public class SigmoidTransferFunction extends TransferFunction {

    private static final long serialVersionUID = -3281218850408730758L;

    private final double mResponse;

    public SigmoidTransferFunction(final double response) {
        mResponse = response;
    }

    @Override
    public double getOutput(final double net) {
        return 1.0 / (1.0 + Math.exp(-net * mResponse));
    }

}
