/**
 * Helicopter Domain  for RL-Competition (2009)
 * RLAI's Port of Pieter Abbeel's code submission.
 *
 * Copyright (C) 2007, Pieter Abbeel
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.rlcommunity.environment.helicopter;

import java.net.URL;
import java.util.Vector;

import nl.uva.ataa.EpisodeRunner;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

//import rlglue.types.Random_seed_key;
//import rlglue.types.Reward_observation;
//import rlglue.types.State_key;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.HasImageInterface;
import rlVizLib.messaging.interfaces.ProvidesEpisodeSummariesInterface;

//import rlVizLib.messaging.interfaces.RLVizVersionResponseInterface;

public abstract class Helicopter extends EnvironmentBase implements HasAVisualizerInterface, HasImageInterface,
        ProvidesEpisodeSummariesInterface {

    Observation o;
    HelicopterState heli = new HelicopterState();
    Reward_observation_terminal ro;
    // Random_seed_key random_seed = null;
    Vector<HelicopterState> savedStates = new Vector<HelicopterState>();

    protected rlVizLib.utilities.logging.EpisodeLogger theEpisodeLogger;

    // Use the logging interface to sends back to the server information
    // about the whole run:
    protected boolean recordLogs = true;

    public boolean allowSaveLoadState = true;
    public boolean allowSaveLoadSeed = true;

    public Helicopter() {
        this(getDefaultParameters());
    }

    public Helicopter(final ParameterHolder p) {
        if (p != null) {
            if (!p.isNull()) {
                setWindWaveNS(p.getDoubleParam("windNSMaxStr"), p.getDoubleParam("windNSHz"),
                        p.getDoubleParam("windNSPhase"), p.getDoubleParam("windNSCenterAmp"));
                setWindWaveEW(p.getDoubleParam("windEWMaxStr"), p.getDoubleParam("windEWHz"),
                        p.getDoubleParam("windEWPhase"), p.getDoubleParam("windEWCenterAmp"));
            }
        }
    }

    public static TaskSpecPayload getTaskSpecPayLoad(final ParameterHolder p) {
        // Superhacky
        final Helicopter theWorld = new HelicopterStub(p);
        final String taskSpec = theWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    public static ParameterHolder getDefaultParameters() {
        final ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        p.addDoubleParam("WindNS MaxStr [0.0d,1.0d]", 0.0d);
        p.setAlias("windNSMaxStr", "WindNS MaxStr [0.0d,1.0d]");
        p.addDoubleParam("WindNS Hz [0.0d,1.0d]", 0.0d);
        p.setAlias("windNSHz", "WindNS Hz [0.0d,1.0d]");
        p.addDoubleParam("WindNS Phase [0.0d,1.0d)", 0.0d);
        p.setAlias("windNSPhase", "WindNS Phase [0.0d,1.0d)");
        p.addDoubleParam("WindNS CenterAmp [-1.0d,1.0d]", 0.0d);
        p.setAlias("windNSCenterAmp", "WindNS CenterAmp [-1.0d,1.0d]");
        p.addDoubleParam("WindEW MaxStr [0.0d,1.0d]", 0.0d);
        p.setAlias("windEWMaxStr", "WindEW MaxStr [0.0d,1.0d]");
        p.addDoubleParam("WindEW Hz [0.0d,1.0d]", 0.0d);
        p.setAlias("windEWHz", "WindEW Hz [0.0d,1.0d]");
        p.addDoubleParam("WindEW Phase [0.0d,1.0d)", 0.0d);
        p.setAlias("windEWPhase", "WindEW Phase [0.0d,1.0d)");
        p.addDoubleParam("WindEW CenterAmp [-1.0d,1.0d]", 0.0d);
        p.setAlias("windEWCenterAmp", "WindEW CenterAmp [-1.0d,1.0d]");
        return p;
    }

    /**
     * Establish sinusoidal wind current along NS axis.
     * 
     * n.b., if windHz is 0 then wind is steady at 'windCenterAmp'.
     */
    public void setWindWaveNS(double windMaxStr, // [0, 1], maximum force wind will exert
            double windHz, // [0, 1], number of cycles per second
            final double windPhase, // [0, 1], a fraction of the wave period
            double windCenterAmp) // [0, 1], center amplitude of the sine wave
    {
        assert windMaxStr >= 0;
        assert windMaxStr <= 1;
        assert windHz >= 0;
        assert windHz <= 1;
        assert windPhase >= 0;
        assert windPhase <= 1;
        assert windCenterAmp >= 0;
        assert windCenterAmp <= 1;
        windHz *= HelicopterState.WIND_MAXHZ;
        windMaxStr *= HelicopterState.WIND_MAX;
        windCenterAmp *= HelicopterState.WIND_MAX;
        // derive a scalar ("windAmpNS") for the sine wave which produces wind
        // with max force = "windMaxStr" and center amplitude = "windCenterAmp"
        heli.windAmpNS = windMaxStr - windCenterAmp;
        if (heli.windAmpNS > HelicopterState.WIND_MAX) heli.windAmpNS = windMaxStr + windCenterAmp;
        // derive angular frequency in radians
        heli.windFreqNS = 2 * Math.PI * windHz;
        // derive the phase (time offset)
        if (windHz != 0) heli.windPhaseNS = windPhase * (heli.windFreqNS / windHz);
        heli.windCenterNS = windCenterAmp;
    }

    /**
     * Establish sinusoidal wind current along EW axis.
     * 
     * n.b., if windHz is 0 then wind is steady at 'windCenterAmp'.
     */
    public void setWindWaveEW(double windMaxStr, // [0, 1], maximum force wind will exert
            double windHz, // [0, 1], number of cycles per second
            final double windPhase, // [0, 1], a fraction of the wave period
            double windCenterAmp) // [0, 1], center amplitude of the sine wave
    {
        assert windMaxStr >= 0;
        assert windMaxStr <= 1;
        assert windHz >= 0;
        assert windHz <= 1;
        assert windPhase >= 0;
        assert windPhase <= 1;
        assert windCenterAmp >= 0;
        assert windCenterAmp <= 1;
        windHz *= HelicopterState.WIND_MAXHZ;
        windMaxStr *= HelicopterState.WIND_MAX;
        windCenterAmp *= HelicopterState.WIND_MAX;
        // derive a scalar ("windAmpEW") for the sine wave which produces wind
        // with max force = "windMaxStr" and center amplitude = "windCenterAmp"
        heli.windAmpEW = windMaxStr - windCenterAmp;
        if (heli.windAmpEW > HelicopterState.WIND_MAX) heli.windAmpEW = windMaxStr + windCenterAmp;
        // derive angular frequency in radians
        heli.windFreqEW = 2 * Math.PI * windHz;
        // derive the phase (time offset)
        if (windHz != 0) heli.windPhaseEW = windPhase * (heli.windFreqEW / windHz);
        heli.windCenterEW = windCenterAmp;
    }

    /**
     * Set a steady NS wind. Use setWindWaveNS with 0 Hz.
     * 
     * @Deprecated
     */
    public void setWind0(final double newValue) {
        setWindWaveNS(newValue, 0, 0, newValue);
    }

    /**
     * Set a steady EW wind. Use setWindWaveNS with 0 Hz.
     * 
     * @Deprecated
     */
    public void setWind1(final double newValue) {
        setWindWaveEW(newValue, 0, 0, newValue);
    }

    /**
     * @Deprecated
     */
    public String getOldTaskSpec() {
        final String oldSpec = String
                .format("2:e:12_[f,f,f,f,f,f,f,f,f,f,f,f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]:4_[f,f,f,f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]:[]",
                        HelicopterState.MAX_VEL, HelicopterState.MAX_VEL, HelicopterState.MAX_VEL,
                        HelicopterState.MAX_VEL, HelicopterState.MAX_VEL, HelicopterState.MAX_VEL,
                        HelicopterState.MAX_POS, HelicopterState.MAX_POS, HelicopterState.MAX_POS,
                        HelicopterState.MAX_POS, HelicopterState.MAX_POS, HelicopterState.MAX_POS,
                        HelicopterState.MAX_RATE, HelicopterState.MAX_RATE, HelicopterState.MAX_RATE,
                        HelicopterState.MAX_RATE, HelicopterState.MAX_RATE, HelicopterState.MAX_RATE,
                        HelicopterState.MAX_QUAT, HelicopterState.MAX_QUAT, HelicopterState.MAX_QUAT,
                        HelicopterState.MAX_QUAT, HelicopterState.MAX_QUAT, HelicopterState.MAX_QUAT,
                        HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION,
                        HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION,
                        HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION);
        return oldSpec;
    }

    @Override
    public String env_init() {
        if (recordLogs) theEpisodeLogger = new rlVizLib.utilities.logging.EpisodeLogger();
        /* initializing the map struct and an observation object */
        String Task_spec = "";
        o = new Observation(0, 12);

        /* Create and Return task specification */
        Task_spec = makeTaskSpec();

        return Task_spec;
    }

    @Override
    @Deprecated
    public Vector<String> getEpisodeSummary() {
        return null;
    }

    @Override
    public String getEpisodeSummary(final long charToStartOn, final int charsToSend) {
        return theEpisodeLogger.getLogSubString(charToStartOn, charsToSend);
    }

    private String makeTaskSpec() {

        final TaskSpecVRLGLUE3 taskSpecObject = new TaskSpecVRLGLUE3();
        taskSpecObject.setEpisodic();
        taskSpecObject.setDiscountFactor(1.0d);
        // The 3 velocities: forward, sideways, up
        // # forward velocity
        // # sideways velocity (to the right)
        // # downward velocity
        taskSpecObject.addContinuousObservation(new DoubleRange(-HelicopterState.MAX_VEL, HelicopterState.MAX_VEL, 3));
        // # helicopter x-coord position - desired x-coord position -- helicopter's x-axis points forward
        // # helicopter y-coord position - desired y-coord position -- helicopter's y-axis points to the right
        // # helicopter z-coord position - desired z-coord position -- helicopter's z-axis points down
        taskSpecObject.addContinuousObservation(new DoubleRange(-HelicopterState.MAX_POS, HelicopterState.MAX_POS, 3));
        // # angular rate around helicopter's x axis
        // # angular rate around helicopter's y axis
        // # angular rate around helicopter's z axis
        taskSpecObject
                .addContinuousObservation(new DoubleRange(-HelicopterState.MAX_RATE, HelicopterState.MAX_RATE, 3));
        // quaternion x,y,z entries
        taskSpecObject
                .addContinuousObservation(new DoubleRange(-HelicopterState.MAX_QUAT, HelicopterState.MAX_QUAT, 3));

        taskSpecObject.addContinuousAction(new DoubleRange(-HelicopterState.MAX_ACTION, HelicopterState.MAX_ACTION, 4));
        // Apparently we're not specifying the rewards
        final DoubleRange theRewardRange = new DoubleRange(0, 0);
        theRewardRange.setMinUnspecified();
        taskSpecObject.setRewardRange(theRewardRange);
        final String newTaskSpecString = taskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(newTaskSpecString);

        return newTaskSpecString;
    }

    @Override
    public Observation env_start() {
        if (recordLogs) {
            theEpisodeLogger.clear();
        }

        // start at origin, zero velocity, zero angular rate, perfectly level and facing north
        heli.reset();

        if (recordLogs) {
            final String stateSerialized = heli.stringSerialize();
            theEpisodeLogger.appendLogString(stateSerialized);
        }
        return makeObservation();
    }

    public Observation env_start(final double[] startState) {
        if (recordLogs) {
            theEpisodeLogger.clear();
        }

        heli.reset(startState);

        if (recordLogs) {
            final String stateSerialized = heli.stringSerialize();
            theEpisodeLogger.appendLogString(stateSerialized);
        }
        return makeObservation();
    }

    @Override
    public Reward_observation_terminal env_step(final Action action) {

        heli.stateUpdate(action);
        heli.num_sim_steps++;
        heli.env_terminal = heli.env_terminal || (heli.num_sim_steps == EpisodeRunner.EPISODE_LENGTH);

        int isTerminal = 0;
        if (heli.env_terminal) {
            isTerminal = 1;
        }

        ro = new Reward_observation_terminal(getReward(), makeObservation(), isTerminal);
        if (recordLogs)
            theEpisodeLogger.appendLogString(heli.stringSerialize() + "_a1=" + action.doubleArray[0] + "_a2="
                    + action.doubleArray[1] + "_a3=" + action.doubleArray[2] + "_a4=" + action.doubleArray[3] + "_r="
                    + ro.r + "_terminal=" + isTerminal);
        return ro;
    }

    @Override
    public void env_cleanup() {
        if (savedStates != null) savedStates.clear();
    }

    @Override
    public String env_message(final String theMessage) {

        EnvironmentMessages theMessageObject;

        // RL-compatible?
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (final NotAnRLVizMessageException e) {
            System.err.println("Someone sent Helicopter a message that wasn't " + "RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) return theMessageObject.handleAutomatically(this);

        System.err.println("We need some code written in Env Message for  " + " Helicopter. unknown request received: "
                + theMessage);
        Thread.dumpStack();
        return null;
    }

    // goal state is all zeros, quadratically penalize for deviation:
    double getReward() {
        double reward = 0;
        if (!heli.env_terminal) { // not in terminal state
            reward -= heli.velocity.x * heli.velocity.x;
            reward -= heli.velocity.y * heli.velocity.y;
            reward -= heli.velocity.z * heli.velocity.z;
            reward -= heli.position.x * heli.position.x;
            reward -= heli.position.y * heli.position.y;
            reward -= heli.position.z * heli.position.z;
            reward -= heli.angular_rate.x * heli.angular_rate.x;
            reward -= heli.angular_rate.y * heli.angular_rate.y;
            reward -= heli.angular_rate.z * heli.angular_rate.z;
            reward -= heli.q.x * heli.q.x;
            reward -= heli.q.y * heli.q.y;
            reward -= heli.q.z * heli.q.z;
        } else { // in terminal state, obtain very negative reward b/c the agent will exit, we have to give out reward
                 // for all future times
            reward = -3.0f
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
            reward *= EpisodeRunner.EPISODE_LENGTH - heli.num_sim_steps;

            // System.out.println("Final reward is: "+reward+" NUM_SIM_STEPS_PER_EPISODE="+HelicopterState.NUM_SIM_STEPS_PER_EPISODE
            // +"  heli.num_sim_steps="+ heli.num_sim_steps);
        }
        return reward;
    }

    // This method creates the object that can be used to easily set different problem parameters
    @Override
    protected Observation makeObservation() {
        return heli.makeObservation();
    }

    public RLVizVersion getTheVersionISupport() {
        return new RLVizVersion(1, 0);
    }

    public double getMaxValueForQuerableVariable(final int dimension) {
        switch (dimension) {
            case 0:
                return HelicopterState.MAX_VEL;
            case 1:
                return HelicopterState.MAX_VEL;
            case 2:
                return HelicopterState.MAX_VEL;
            case 3:
                return HelicopterState.MAX_POS;
            case 4:
                return HelicopterState.MAX_POS;
            case 5:
                return HelicopterState.MAX_POS;
            case 6:
                return HelicopterState.MAX_RATE;
            case 7:
                return HelicopterState.MAX_RATE;
            case 8:
                return HelicopterState.MAX_RATE;
            case 9:
                return HelicopterState.MAX_QUAT;
            case 10:
                return HelicopterState.MAX_QUAT;
            case 11:
                return HelicopterState.MAX_QUAT;
            case 12:
                return HelicopterState.MAX_QUAT;
            default:
                System.out.println("Invalid Dimension in getMaxValueForQuerableVariable for Helicopter");
                return Double.POSITIVE_INFINITY;
        }
    }

    public double getMinValueForQuerableVariable(final int dimension) {
        switch (dimension) {
            case 0:
                return -HelicopterState.MAX_VEL;
            case 1:
                return -HelicopterState.MAX_VEL;
            case 2:
                return -HelicopterState.MAX_VEL;
            case 3:
                return -HelicopterState.MAX_POS;
            case 4:
                return -HelicopterState.MAX_POS;
            case 5:
                return -HelicopterState.MAX_POS;
            case 6:
                return -HelicopterState.MAX_RATE;
            case 7:
                return -HelicopterState.MAX_RATE;
            case 8:
                return -HelicopterState.MAX_RATE;
            case 9:
                return -HelicopterState.MAX_QUAT;
            case 10:
                return -HelicopterState.MAX_QUAT;
            case 11:
                return -HelicopterState.MAX_QUAT;
            case 12:
                return -HelicopterState.MAX_QUAT;
            default:
                System.out.println("Invalid Dimension in getMaxValueForQuerableVariable for Helicopter");
                return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public String getVisualizerClassName() {
        return "org.rlcommunity.environment.helicopter.visualizers.HelicopterVisualizer";
    }

    @Override
    public URL getImageURL() {
        final URL imageURL = Helicopter.class.getResource("/images/helicopter.png");
        return imageURL;
    }

}

/**
 * Brian Tanner Feb 1 2009 This is the quickest hack ever.
 */
class HelicopterStub extends Helicopter implements rlVizLib.dynamicLoading.Unloadable {
    public HelicopterStub(final ParameterHolder P) {
        super(P);
    }
}

class DetailsProvider implements hasVersionDetails {

    @Override
    public String getName() {
        return "Helicopter Hovering 1.0";
    }

    @Override
    public String getShortName() {
        return "Helicopter";
    }

    @Override
    public String getAuthors() {
        return "Pieter Abbeel, Mark Lee, Brian Tanner, Chris Rayner";
    }

    @Override
    public String getInfoUrl() {
        return "http://rl-competition.org";
    }

    @Override
    public String getDescription() {
        return "Helicopter Hovering Reinforcement Learning Problem.";
    }
}
