package nl.uva.ataa.environment;

import java.util.LinkedList;
import java.util.List;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public abstract class Predictor implements EnvironmentInterface {

    /** The environment used for tests in the current episode */
    private WindEnvironment mCurrentEnvironment;

    /** The rewards given per episode */
    private final List<Double> mEpisodeRewards = new LinkedList<>();

    /** The rewards given in the current episode */
    private double mEpisodeReward = 0.0;

    /** The rewards that the environment has given during tests */
    private double mAccumulatedReward = 0.0;

    /** The amount of episodes ran */
    private int mNumEpisodes = 0;

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
        mEpisodeReward = 0.0;

        generateNewEnvironment();

        return mCurrentEnvironment.env_init();
    }

    @Override
    public Observation env_start() {
        ++mNumEpisodes;

        return mCurrentEnvironment.env_start();
    }

    @Override
    public Reward_observation_terminal env_step(final Action action) {
        final Reward_observation_terminal rewObs = mCurrentEnvironment.env_step(action);

        mEpisodeReward += rewObs.getReward();

        // Make note of the reward this run
        if (rewObs.isTerminal()) {
            mEpisodeRewards.add(mEpisodeReward);
            mAccumulatedReward += mEpisodeReward;
        }

        return rewObs;
    }

    @Override
    public void env_cleanup() {
        mEpisodeReward = 0.0;
        mAccumulatedReward = 0.0;
        mNumEpisodes = 0;

        if (mCurrentEnvironment != null) {
            mCurrentEnvironment.env_cleanup();
        }
    }

    @Override
    public String env_message(final String theMessage) {
        return mCurrentEnvironment.env_message(theMessage);
    }

    /**
     * @return The rewards given per episode
     */
    public List<Double> getEpisodeRewards() {
        return mEpisodeRewards;
    }

    /**
     * @return The rewards that the environment has given during tests
     */
    public double getAccumulatedReward() {
        return mAccumulatedReward;
    }

    /**
     * @return The amount of episodes ran
     */
    public int getNumEpisodes() {
        return mNumEpisodes;
    }

}
