package nl.uva.ataa.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import nl.uva.ataa.environment.fitness.FitnessFunction;
import nl.uva.ataa.utilities.Utilities;

public class BaselinePredictor extends DiscretePredictor {

    /** The file to save the states to */
    private final static String STATE_FILE = "helicopter_start_states.dat";

    /** The amount of total baselines to test against */
    private final int mBaselineAmount;

    /** All start states */
    private final List<double[]> mStartStates;

    /** The currently used state */
    private int mCurrentState = 0;

    /**
     * Construct a baseline predictor with the given baseline
     * 
     * @param numParamValues
     *            The amount of discrete values per parameters
     * @param fitnessFunction
     *            The fitness function used to evolve the predictor
     */
    public BaselinePredictor(final int numParamValues, final int baselineAmount, final FitnessFunction fitnessFunction) {
        super(numParamValues, fitnessFunction);
        mBaselineAmount = baselineAmount;
        mStartStates = getStartStates(mBaselineAmount, numParamValues);
    }

    /**
     * Get all start states. If no start states where already generated, generate new start states
     * 
     * @param stateAmount
     *            The amount of start states to generate
     * @param numParamValues
     *            The number of discrete values a parameter can have
     * @return A list of all start states
     */
    @SuppressWarnings("unchecked")
    private static List<double[]> getStartStates(final int stateAmount, final int numParamValues) {
        List<double[]> startStates = null;

        final File f = new File(STATE_FILE);
        if (f.exists() && f.isFile()) {
            try {
                final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                startStates = (List<double[]>) ois.readObject();
                ois.close();
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Make sure there are enough states
            if (startStates.size() >= stateAmount) {
                return startStates;
            }
        }

        startStates = generateStartstates(stateAmount, numParamValues);

        return startStates;
    }

    /**
     * Generate the start states with the given parameters and save the to disk
     * 
     * @param stateAmount
     *            The amount of start states to generate
     * @param numParamValues
     *            The number of discrete values a parameter can have
     * @return A list of all generated start states
     */
    private static List<double[]> generateStartstates(final int stateAmount, final int numParamValues) {
        final List<double[]> startStates = new LinkedList<>();

        for (int stateNum = 0; stateNum < stateAmount; stateNum++) {
            final double[] startState = new double[12];
            for (int i = 0; i < 9; ++i) {
                startState[i] = StatePredictor.sampleToParam(i, Utilities.RNG.nextDouble(), numParamValues);
            }
            for (int i = 9; i < 12; ++i) {
                startState[i] = 0.5;
            }
            startStates.add(startState);
        }

        try {
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE));
            oos.writeObject(startStates);
            oos.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return startStates;
    }

    /**
     * Get the total number of baselines in this predictor
     * 
     * @return The total amount of baselines
     */
    public int getTotalNumberOfBaselines() {
        return mBaselineAmount;
    }

    @Override
    public String env_init() {
        setStartState(mStartStates.get(mCurrentState++));

        return super.env_init();
    }

    @Override
    public void env_cleanup() {
        super.env_cleanup();

        mCurrentState = 0;
    }

    @Override
    protected double getSample(final int index) {
        return 0.5;
    }
}
