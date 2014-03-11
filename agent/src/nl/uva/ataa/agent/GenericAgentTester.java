package nl.uva.ataa.agent;

import java.util.List;
import java.util.Locale;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import nl.uva.ataa.environment.BetaPredictor;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.Wind;
import nl.uva.ataa.environment.WindEnvironment;
import nl.uva.ataa.environment.fitness.RewardFitness;
import nl.uva.ataa.evolver.PredictorEvolver;
import nl.uva.ataa.utilities.Utilities;

public class GenericAgentTester {

    /**
     * @param args
     */
    public static void main(String[] args) {

        final int numPredictors = 20;
        final int episodeLength = 1000;

        // set up the generic agent from Shimon's research
        ShimonsAgent genericAgent = new ShimonsAgent();
        genericAgent.setWeights(new double[] { -0.7118, 0.3188, -1.2159, 2.3283, 0.1129, -1.1902, -0.5062, 0.5478,
                -0.7646, -0.2285, -2.5869, -0.225, 0.8412, -1.6608, -0.2988, 2.937, 0.0, 1.3106, -0.2752, 0.2084,
                -2.3259, -0.3596, 0.3116, 0.773, 0.7982, -0.5549, -0.0631, 2.0086, -0.6239, -1.346, 0.2472, 1.4733,
                1.3579, 0.3694, 0.0288, 1.808, 0.4698, -1.0696, -0.178, 0.0742, -0.0004, -1.105 });

        // set up wind environment (from Shimon's research, test MDP 0)
        Wind windNS = new Wind(0.693851, 0.115374, 0.669592, 0.588185);
        Wind windEW = new Wind(0.853936, 0.252569, 0.757596, 0.367433);
        WindEnvironment windEnv = new WindEnvironment(windNS, windEW);

        // run a tests
        // Initialise the parties
        genericAgent.agent_init(windEnv.env_init());

        // Perform the first step in the episode
        final Observation initObs = windEnv.env_start();
        Action action = genericAgent.agent_start(initObs);
        Reward_observation_terminal rewObs = null;

        // Perform steps until the episode is over or the state is terminal
        for (int timestep = 0; timestep < episodeLength; timestep++) {
            rewObs = windEnv.env_step(action);
            if (rewObs.isTerminal()) {
                System.out.println("Crashed after: " + timestep);
                break;
            }
            action = genericAgent.agent_step(rewObs.getReward(), rewObs.getObservation());

        }
        genericAgent.agent_end(rewObs.getReward());

        // print out results
        System.out.println("Average reward: " + Math.round(genericAgent.getAverageReward()) + " , Average rounds: "
                + Math.round(genericAgent.getAverageNumSteps()));
    }

}
