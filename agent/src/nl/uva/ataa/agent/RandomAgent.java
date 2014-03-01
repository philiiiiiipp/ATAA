package nl.uva.ataa.agent;

/* Random Agent that works in all domains
 * Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */

import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class RandomAgent implements AgentInterface {
    private Action action;
    private final Random random = new Random();

    TaskSpec TSO = null;

    public RandomAgent() {}

    @Override
    public void agent_cleanup() {}

    @Override
    public void agent_end(final double arg0) {

    }

    public void agent_freeze() {

    }

    @Override
    public void agent_init(final String taskSpec) {
        TSO = new TaskSpec(taskSpec);
        if (TSO.getVersionString().equals("Mario-v1")) {
            final TaskSpecVRLGLUE3 hardCodedTaskSpec = new TaskSpecVRLGLUE3();
            hardCodedTaskSpec.setEpisodic();
            hardCodedTaskSpec.setDiscountFactor(1.0d);
            // Run
            hardCodedTaskSpec.addDiscreteAction(new IntRange(-1, 1));
            // Jump
            hardCodedTaskSpec.addDiscreteAction(new IntRange(0, 1));
            // Speed
            hardCodedTaskSpec.addDiscreteAction(new IntRange(0, 1));
            TSO = new TaskSpec(hardCodedTaskSpec);
        }
        action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
    }

    @Override
    public String agent_message(final String arg0) {
        return null;
    }

    @Override
    public Action agent_start(final Observation o) {
        randomify(action);
        return action;
    }

    @Override
    public Action agent_step(final double arg0, final Observation o) {
        randomify(action);
        return action;
    }

    private void randomify(final Action action) {
        for (int i = 0; i < TSO.getNumDiscreteActionDims(); i++) {
            final IntRange thisActionRange = TSO.getDiscreteActionRange(i);
            action.intArray[i] = random.nextInt(thisActionRange.getRangeSize()) + thisActionRange.getMin();
        }
        for (int i = 0; i < TSO.getNumContinuousActionDims(); i++) {
            final DoubleRange thisActionRange = TSO.getContinuousActionRange(i);
            action.doubleArray[i] = random.nextDouble() * (thisActionRange.getRangeSize()) + thisActionRange.getMin();
        }
    }

    public static void main(final String[] args) {
        final AgentLoader L = new AgentLoader(new RandomAgent());
        L.run();
    }

}
