package nl.uva.ataa.agent;

import org.neuroph.core.Connection;
import org.neuroph.core.input.InputFunction;

public class SumInputFunction extends InputFunction {

    private static final long serialVersionUID = 8215088724591743367L;

    @Override
    public double getOutput(final Connection[] inputConnections) {
        double output = 0;
        for (final Connection input : inputConnections) {
            output += input.getWeightedInput();
        }
        return output;
    }

}
