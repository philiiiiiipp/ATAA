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

public class BaselinePredictor extends StatePredictor {

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

        File f = new File(STATE_FILE);
        if (f.exists() && f.isFile()) {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                startStates = (List<double[]>) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            startStates = generateStartstates(stateAmount, numParamValues);
        }

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
        List<double[]> startStates = new LinkedList<>();

        for (int stateNum = 0; stateNum < stateAmount; stateNum++) {
            final double[] startState = new double[12];
            for (int i = 0; i < 9; ++i) {
                final double modifier = MODIFIERS[i / 3];
                final double sample = Math.floor(Utilities.RNG.nextDouble() * numParamValues) / (numParamValues - 1);
                startState[i] = sample * (2 - BORDER_RANGE * 2) * modifier - modifier + BORDER_RANGE;
            }
            startStates.add(startState);
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE));
            oos.writeObject(startStates);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        return super.env_init() + " BIAS: " + 1;
    }

    @Override
    public void env_cleanup() {
        super.env_cleanup();

        mCurrentState = 0;
    }
}
