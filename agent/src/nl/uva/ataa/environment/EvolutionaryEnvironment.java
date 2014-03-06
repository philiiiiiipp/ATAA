package nl.uva.ataa.environment;

import java.util.Random;

import org.rlcommunity.environment.helicopter.Helicopter;
import org.rlcommunity.environment.helicopter.HelicopterState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class EvolutionaryEnvironment extends Helicopter {

    /** The rewards that the environment has given during tests */
    private double mAccumulatedReward = 0.0;

    /** The nr of episodes run during tests */
    private int mNrEpisodes = 0;

    /**
     * Creates an environment with random wind settings
     */
    public EvolutionaryEnvironment() {
        final Random rand = new Random();
        setWindWaveNS(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        setWindWaveEW(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    /**
     * Creates an environment with the given wind settings
     * 
     * @param windNS
     *            Wind for the North-South-axis
     * @param windEW
     *            Wind for the East-West-axis
     */
    public EvolutionaryEnvironment(final Wind windNS, final Wind windEW) {
        setWindWaveNS(windNS.getMaxStr(), windNS.getHz(), windNS.getPhase(), windNS.getCenterAmp());
        setWindWaveEW(windEW.getMaxStr(), windEW.getHz(), windEW.getPhase(), windEW.getCenterAmp());
    }

    @Override
    public Observation env_start() {
        mNrEpisodes++;
        return super.env_start();
    }

    @Override
    public Reward_observation_terminal env_step(final Action action) {
        final Reward_observation_terminal rewObs = super.env_step(action);
        mAccumulatedReward += rewObs.getReward();
        return rewObs;
    }

    @Override
    public void env_cleanup() {
        mAccumulatedReward = 0.0;
        mNrEpisodes = 0;
        super.env_cleanup();
    }

    /**
     * Returns the fitness of the environment based on the last series of tests it has performed.
     * 
     * @return The environments's fitness
     */
    public double getFitness() {
        return -mAccumulatedReward / mNrEpisodes;
    }

    /**
     * Calculates the worst reward that's possible to get in an episode.
     * 
     * @return The minimum reward
     */
    public double getMinimimReward() {
        return -3.0f
                * HelicopterState.MAX_POS
                * HelicopterState.MAX_POS
                + -3.0f
                * HelicopterState.MAX_RATE
                * HelicopterState.MAX_RATE
                + -3.0f
                * HelicopterState.MAX_VEL
                * HelicopterState.MAX_VEL
                - (1.0f - HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE
                        * HelicopterState.MIN_QW_BEFORE_HITTING_TERMINAL_STATE);
    }
}
