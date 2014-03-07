package nl.uva.ataa.environment;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public abstract class Predictor implements EnvironmentInterface {

    /** The environment used for tests in the current episode */
    private WindEnvironment mCurrentEnvironment;

    /** The rewards that the environment has given during tests */
    private double mAccumulatedReward = 0.0;

    /** The nr of episodes run during tests */
    private int mNrEpisodes = 0;

    /**
     * Generates a random environment, based on the implementation's distribution. The new environment will be used in
     * the next episode.
     * 
     * @return A random environment
     */
    private void generateNewEnvironment() {
        final Wind windNS = new Wind(getSample(0), getSample(1), getSample(2), getSample(3));
        final Wind windEW = new Wind(getSample(4), getSample(5), getSample(6), getSample(7));
        mCurrentEnvironment = new WindEnvironment(windNS, windEW);
    }

    /**
     * Must return a sample in [0, 1] to be used as a wind parameter.
     * 
     * @param index
     *            The index of the sample, ranging from 0 to 7, corresponding to the the two winds' parameters
     * @return A sample in [0, 1]
     */
    protected abstract double getSample(final int index);

    @Override
    public String env_init() {
        generateNewEnvironment();

        return mCurrentEnvironment.env_init();
    }

    @Override
    public Observation env_start() {
        mNrEpisodes++;

        return mCurrentEnvironment.env_start();
    }

    @Override
    public Reward_observation_terminal env_step(final Action action) {
        final Reward_observation_terminal rewObs = mCurrentEnvironment.env_step(action);

        mAccumulatedReward += rewObs.getReward();

        return rewObs;
    }

    @Override
    public void env_cleanup() {
        mAccumulatedReward = 0.0;
        mNrEpisodes = 0;

        if (mCurrentEnvironment != null) {
            mCurrentEnvironment.env_cleanup();
        }
    }

    @Override
    public String env_message(final String theMessage) {
        return mCurrentEnvironment.env_message(theMessage);
    }

    /**
     * Returns the fitness of the environment based on the last series of tests it has performed.
     * 
     * @return The environments's fitness
     */
    public double getFitness() {
        return -mAccumulatedReward / mNrEpisodes;
    }
}
