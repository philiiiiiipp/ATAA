/**
 * Helicopter Domain for RL-Competition:
 * RLAI's Port of Pieter Abbeel's code submission
 * Copyright (C) 2007, Pieter Abbeel.
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

import java.util.Random;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class HelicopterState {

    private final Random randomNumberGenerator = new Random();

    // Constant indices into observations' doubleArray:
    static final int ndot_idx = 0; // north velocity
    static final int edot_idx = 1; // east velocity
    static final int ddot_idx = 2; // down velocity
    static final int n_idx = 3; // north
    static final int e_idx = 4; // east
    static final int d_idx = 5; // down
    static final int p_idx = 6; // angular rate around forward axis
    static final int q_idx = 7; // angular rate around sideways (to the right) axis
    static final int r_idx = 8; // angular rate around vertical (downward) axis
    static final int qx_idx = 9; // quaternion entries, x,y,z,w q = [ sin(theta/2) * axis;
    static final int qy_idx = 10; // cos(theta/2)] where axis = axis of rotation; theta is amount
    static final int qz_idx = 11; // of rotation around that axis [recall: any rotation can be
    static final int qw_idx = 12; // represented by a single rotation around some axis]
    static final int state_size = 13; // <---deprecated?
    static final int NUMOBS = state_size - 1; // (qw_idx is not observed)

    // Upper bounds on values state variables can take on (required by rl_glue
    // to be put into a task_spec string at environment initialization):
    public static final double MAX_VEL = 5.0;

    public static final double MAX_POS = 20.0;

    public static final double MAX_RATE = 2 * 3.1415 * 2;

    public static final double MAX_QUAT = 1.0;

    public static final double MIN_QW_BEFORE_HITTING_TERMINAL_STATE = Math.cos(30.0 / 2.0 * Math.PI / 180.0);

    static final double MAX_ACTION = 1.0;
    static double mins[] = { -MAX_VEL, -MAX_VEL, -MAX_VEL, -MAX_POS, -MAX_POS, -MAX_POS, -MAX_RATE, -MAX_RATE,
            -MAX_RATE, -MAX_QUAT, -MAX_QUAT, -MAX_QUAT, -MAX_QUAT };
    static double maxs[] = { MAX_VEL, MAX_VEL, MAX_VEL, MAX_POS, MAX_POS, MAX_POS, MAX_RATE, MAX_RATE, MAX_RATE,
            MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT };

    // Very crude helicopter model, okay around hover:
    private final double heli_model_u_drag = 0.18, heli_model_v_drag = 0.43, heli_model_w_drag = 0.49,
            heli_model_p_drag = 12.78, heli_model_q_drag = 10.12, heli_model_r_drag = 8.16, heli_model_u0_p = 33.04,
            heli_model_u1_q = -33.32, heli_model_u2_r = 70.54, heli_model_u3_w = -42.15,
            heli_model_tail_rotor_side_thrust = -0.54;

    // Simulation constants:
    // Time scale [time scale for control --- internally we
    // integrate at 100Hz for simulating the dynamics]:
    public final double DT_CONTROL = .1;
    // after 6000 steps we automatically enter the terminal state:
    // public final static int NUM_SIM_STEPS_PER_EPISODE = 6000; See EpisodeRunner instead

    // Internal Helicopter state variables:
    public HeliVector velocity = new HeliVector(0.0d, 0.0d, 0.0d);
    public HeliVector position = new HeliVector(0.0d, 0.0d, 0.0d);
    public HeliVector angular_rate = new HeliVector(0.0d, 0.0d, 0.0d);
    public Quaternion q = new Quaternion(0.0d, 0.0d, 0.0d, 1.0d);
    public double noise[] = new double[6];
    public boolean env_terminal = false;
    public int num_sim_steps = 0;

    /**
     * New (RL-Comp 2009) variables which control the wind!
     */
    static final double WIND_MAX = 5.0; // <-- 5.0 was the max in 2008
    static final double WIND_MAXHZ = 10.0; // 10Hz is how fast agent can act
    public double wind[] = new double[2]; // current wind values
    public double windAmpNS, windFreqNS, windPhaseNS, windCenterNS;
    public double windAmpEW, windFreqEW, windPhaseEW, windCenterEW;

    /**
     * Default constructor.
     */
    public HelicopterState() {}

    /**
     * Copy constructor: reproduces supplied HelicopterState.
     */
    public HelicopterState(final HelicopterState stateToCopy) {

        velocity = new HeliVector(stateToCopy.velocity);
        position = new HeliVector(stateToCopy.position);
        angular_rate = new HeliVector(stateToCopy.angular_rate);
        q = new Quaternion(stateToCopy.q);

        for (int i = 0; i < 6; i++)
            noise[i] = stateToCopy.noise[i];

        num_sim_steps = stateToCopy.num_sim_steps;
        env_terminal = stateToCopy.env_terminal;
    }

    /**
     * Zero out helicopter state values---a fresh start.
     */
    public void reset() {
        velocity = new HeliVector(0.0d, 0.0d, 0.0d);
        position = new HeliVector(0.0d, 0.0d, 0.0d);
        angular_rate = new HeliVector(0.0d, 0.0d, 0.0d);
        q = new Quaternion(0.0d, 0.0d, 0.0d, 1.0);
        num_sim_steps = 0;
        env_terminal = false;
    }

    public void reset(final double[] params) {
        velocity = new HeliVector(params[0], params[1], params[2]);
        position = new HeliVector(params[3], params[4], params[5]);
        angular_rate = new HeliVector(params[6], params[7], params[8]);
        q = new Quaternion(params[9], params[10], params[11], 1.0);
        num_sim_steps = 0;
        env_terminal = false;
    }

    /**
     * Enforces observation constraints (if anything falls outside the min/max bounds, it is reset to the nearest
     * boundary value).
     */
    private void enforceObservationConstraints(final double observationDoubles[]) {
        for (int i = 0; i < NUMOBS; ++i) {
            if (observationDoubles[i] > maxs[i]) observationDoubles[i] = maxs[i];
            if (observationDoubles[i] < mins[i]) observationDoubles[i] = mins[i];
        }
    }

    /**
     * Return an Observation object, which is the "error state" (i.e., amount of deviation from the goal state).
     * Observation is the error state in the helicopter's coordinate system so that errors/observations can be mapped
     * more directly to actions.
     *
     * Observation (12 vars) consists of: u, v, w : velocities in helicopter frame xerr, yerr, zerr : position error
     * expressed in frame attached to helicopter [xyz correspond to ned when helicopter is in neutral orientation, level
     * and facing north p, q, r : angular rate of change qx, qy, qz : quaternions representing helicopter's rotation
     */
    public Observation makeObservation() {
        final Observation o = new Observation(0, NUMOBS);
        final HeliVector ned_error_in_heli_frame = position.express_in_quat_frame(q);
        final HeliVector uvw = velocity.express_in_quat_frame(q);
        // System.out.println("New position: [" +
        // Double.toString(position.x) + "," +
        // Double.toString(position.y) + "," +
        // Double.toString(position.z) + "]");
        // System.out.println("Error position: [" +
        // Double.toString(ned_error_in_heli_frame.x) + "," +
        // Double.toString(ned_error_in_heli_frame.y) + "," +
        // Double.toString(ned_error_in_heli_frame.z) + "]");

        o.doubleArray[ndot_idx] = uvw.x; // north velocity
        o.doubleArray[edot_idx] = uvw.y; // east velocity
        o.doubleArray[ddot_idx] = uvw.z; // down velocity
        o.doubleArray[n_idx] = ned_error_in_heli_frame.x; // north
        o.doubleArray[e_idx] = ned_error_in_heli_frame.y; // east
        o.doubleArray[d_idx] = ned_error_in_heli_frame.z; // down
        o.doubleArray[p_idx] = angular_rate.x; // angular rate around forward axis
        o.doubleArray[q_idx] = angular_rate.y; // ... around sideways (to the right) axis
        o.doubleArray[r_idx] = angular_rate.z; // ... around vertical (downward) axis

        // the error quaternion gets negated, b/c we consider the rotation
        // required to bring the helicopter back to target in the helicopter's
        // frame of reference
        o.doubleArray[qx_idx] = q.x; // quaternion entries, x,y,z,w q = [ sin(theta/2) * axis;
        o.doubleArray[qy_idx] = q.y; // cos(theta/2)] where axis = axis of rotation; theta is amount
        o.doubleArray[qz_idx] = q.z; // of rotation around that axis [recall: any rotation can be
                                     // represented by a single rotation around some axis]

        // make everything fit within specified boundaries
        enforceObservationConstraints(o.doubleArray);
        return o;
    }

    /**
     * Given the specified action 'a', update the Helicopter's state.
     */
    public void stateUpdate(final Action a) {

        // saturate all the actions (i.e., round actions to nearest -1 or +1)
        // b/c the actuators are limited: [real helicopter's saturation is of
        // course somewhat different, depends on swash plate mixing etc ... ]
        for (int i = 0; i < 4; ++i)
            a.doubleArray[i] = MyMin(MyMax(a.doubleArray[i], -1.0), +1.0);

        // standard deviations for normally-distributed noise
        final double noise_std[] = { 0.1941, 0.2975, 0.6058, // u, v, w
                0.1508, 0.2492, 0.0734 }; // p, q, r
        final double noise_mult = 2.0;
        final double noise_memory = .8;

        // generate normally-distributed random noise
        for (int i = 0; i < 6; ++i)
            noise[i] = noise_memory * noise[i] + (1.0d - noise_memory) * noise_mult * box_mull() * noise_std[i];

        // integrate at 100Hz:
        final double dt = .01;
        // (n.b., calls to stateUpdate happen at 10Hz, so 10 updates per
        // stateUpdate call results in 100Hz state dynamics):
        for (int t = 0; t < (int) (DT_CONTROL / dt); ++t) {

            // ----------------------------------------------------------------------
            // *** sinusoidal update to winds ***
            // ----------------------------------------------------------------------
            double simTime = num_sim_steps * DT_CONTROL + t * dt;
            simTime = (int) (simTime * 100);
            simTime /= 100.0;
            // System.err.print("simTime: " + simTime);
            wind[0] = windAmpNS * Math.sin(windFreqNS * simTime + windPhaseNS) + windCenterNS;
            wind[1] = windAmpEW * Math.sin(windFreqEW * simTime + windPhaseEW) + windCenterEW;
            // System.err.println("/windNS=" + wind[0] + "/windEW=" + wind[1]);

            // Euler integration:

            // ----------------------------------------------------------------------
            // *** position ***
            // ----------------------------------------------------------------------
            position.x += dt * velocity.x;
            position.y += dt * velocity.y;
            position.z += dt * velocity.z;
            // System.out.println("New position: [" +
            // Double.toString(position.x) + "," +
            // Double.toString(position.y) + "," +
            // Double.toString(position.z) + "]");

            // ----------------------------------------------------------------------
            // *** forces ***
            // ----------------------------------------------------------------------
            final HeliVector uvw = velocity.express_in_quat_frame(q);
            // System.out.println("uvw: [" + Double.toString(uvw.x) + "," +
            // Double.toString(uvw.y) + "," +
            // Double.toString(uvw.z) + "]");

            // Calculate 'uvw_force_from_heli_over_m', a vector of the forces
            // affecting the helicopter.
            final HeliVector wind_ned = new HeliVector(wind[0], wind[1], 0.0);
            final HeliVector wind_uvw = wind_ned.express_in_quat_frame(q);
            final HeliVector uvw_force_from_heli_over_m = new HeliVector(-heli_model_u_drag * (uvw.x + wind_uvw.x)
                    + noise[0], -heli_model_v_drag * (uvw.y + wind_uvw.y) + heli_model_tail_rotor_side_thrust
                    + noise[1], -heli_model_w_drag * uvw.z + heli_model_u3_w * a.doubleArray[3] + noise[2]);

            // ----------------------------------------------------------------------
            // *** new velocity due to imposed forces ***
            // ----------------------------------------------------------------------
            final HeliVector ned_force_from_heli_over_m = uvw_force_from_heli_over_m.rotate(q);
            velocity.x += dt * ned_force_from_heli_over_m.x;
            velocity.y += dt * ned_force_from_heli_over_m.y;
            velocity.z += dt * (ned_force_from_heli_over_m.z + 9.81d);
            // System.out.println("New velocity: [" +
            // Double.toString(velocity.x) + "," +
            // Double.toString(velocity.y) + "," +
            // Double.toString(velocity.z) + "]");

            // ----------------------------------------------------------------------
            // *** new orientation ***
            // ----------------------------------------------------------------------
            final HeliVector axis_rotation = new HeliVector(angular_rate.x * dt, angular_rate.y * dt, angular_rate.z
                    * dt);
            final Quaternion rot_quat = axis_rotation.to_quaternion();
            q = q.mult(rot_quat);
            // System.out.println("New orientation: [" +
            // Double.toString(this.q.x) + "," +
            // Double.toString(q.y) + "," +
            // Double.toString(q.z) + "," +
            // Double.toString(q.w) + "]");

            // ----------------------------------------------------------------------
            // *** new angular rate ***
            // ----------------------------------------------------------------------
            final double p_dot = -heli_model_p_drag * angular_rate.x + heli_model_u0_p * a.doubleArray[0] + noise[3];
            final double q_dot = -heli_model_q_drag * angular_rate.y + heli_model_u1_q * a.doubleArray[1] + noise[4];
            final double r_dot = -heli_model_r_drag * angular_rate.z + heli_model_u2_r * a.doubleArray[2] + noise[5];
            angular_rate.x += dt * p_dot;
            angular_rate.y += dt * q_dot;
            angular_rate.z += dt * r_dot;
            // System.out.println("New angular rate: [" +
            // Double.toString(this.angular_rate.x) + "," +
            // Double.toString(angular_rate.y) + "," +
            // Double.toString(angular_rate.z) + "]");

            // mayday, mayday...
            if (!env_terminal
                    && (Math.abs(position.x) > MAX_POS || Math.abs(position.y) > MAX_POS
                            || Math.abs(position.y) > MAX_POS || Math.abs(velocity.x) > MAX_VEL
                            || Math.abs(velocity.y) > MAX_VEL || Math.abs(velocity.z) > MAX_VEL
                            || Math.abs(angular_rate.x) > MAX_RATE || Math.abs(angular_rate.y) > MAX_RATE
                            || Math.abs(angular_rate.z) > MAX_RATE || Math.abs(q.w) < MIN_QW_BEFORE_HITTING_TERMINAL_STATE))
                env_terminal = true;
        }

    }

    // ----------------------------------------------------------------------
    // Utility functions (number crunching, etc.) follow:
    // ----------------------------------------------------------------------
    static double MyMin(final double x, final double y) {
        return (x < y ? x : y);
    }

    static double MyMax(final double x, final double y) {
        return (x > y ? x : y);
    }

    public final Random getRandom() {
        return randomNumberGenerator;
    }

    // / Box-Muller method for generating normally distributed RVs.
    public double box_mull() {
        final double x1 = randomNumberGenerator.nextDouble();
        final double x2 = randomNumberGenerator.nextDouble();
        return Math.sqrt(-2.0f * Math.log(x1)) * Math.cos(2.0f * Math.PI * x2);
    }

    public double rand_minus1_plus1() { // deprecated?
        final double x1 = randomNumberGenerator.nextDouble();
        return 2.0f * x1 - 1.0f;
    }

    public double getRandomNumber() { // deprecated?
        return randomNumberGenerator.nextDouble();
    }

    // / Returns a string representation of the current state.
    public String stringSerialize() {
        final StringBuffer b = new StringBuffer();
        b.append("hs_");
        velocity.stringSerialize(b);
        position.stringSerialize(b);
        angular_rate.stringSerialize(b);
        q.stringSerialize(b);
        b.append("_noise");
        for (int i = 0; i < noise.length; i++)
            b.append("_n" + i + "_" + noise[i]);
        return b.toString();
    }
}
