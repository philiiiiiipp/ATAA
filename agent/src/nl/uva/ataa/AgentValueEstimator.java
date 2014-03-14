package nl.uva.ataa;

import nl.uva.ataa.agent.ShimonsAgent;
import nl.uva.ataa.environment.Predictor;
import nl.uva.ataa.environment.UniformPredictor;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/**
 */
public class AgentValueEstimator {

    /** The maximum length of an episode */
    public static final int EPISODE_LENGTH = 15;

    public static void main(final String[] args) {

        // the generic agent from Shimon's paper
        ShimonsAgent baselineAgent = new ShimonsAgent();
        baselineAgent.setWeights(new double[] { -0.7118, 0.3188, -1.2159, 2.3283, 0.1129, -1.1902, -0.5062, 0.5478,
                -0.7646, -0.2285, -2.5869, -0.225, 0.8412, -1.6608, -0.2988, 2.937, 0.0, 1.3106, -0.2752, 0.2084,
                -2.3259, -0.3596, 0.3116, 0.773, 0.7982, -0.5549, -0.0631, 2.0086, -0.6239, -1.346, 0.2472, 1.4733,
                1.3579, 0.3694, 0.0288, 1.808, 0.4698, -1.0696, -0.178, 0.0742, -0.0004, -1.105 });

        // get a uniform predictor
        UniformPredictor uniformPredictor = new UniformPredictor();

        // we have 4^9 = 262,144
        // and we want to find out the true value of the agent
        // which is the return over 15 episodes
        System.out.println("runs , avg return , time");
        long startTime = System.currentTimeMillis();
        int step = 10;
        for (int runs = 10; runs <= 100000; runs = runs + step) {
            double cumulReturn = 0;
            for (int i = 0; i < runs; ++i) {
                cumulReturn += runEpisode(uniformPredictor, baselineAgent);
            }
            double averageReturn = cumulReturn / runs;
            
            int timeTaken = Math.round((System.currentTimeMillis() - startTime) / 1000);
            System.out.println("" + runs + " , " + averageReturn + " , " + timeTaken + " ; " );
                        
            if (runs == 100) step = 50;
            if (runs == 1000) step = 100;
            if (runs == 5000) step = 500;
            if (runs == 10000) step = 1000;
            if (runs == 20000) step = 5000;
            if (runs == 50000) step = 10000;
        }
        System.out.println("avg num steps: " + baselineAgent.getAverageNumSteps());
    }

    /**
     * Perform one glue step with a given predictor and agent
     * 
     * @param predictor
     *            The given predictor
     * @param agent
     *            The given agent
     */
    private static double runEpisode(final Predictor predictor, final AgentInterface agent) {
        agent.agent_init(predictor.env_init());

        // Perform the first step in the episode
        final Observation initObs = predictor.env_start();
        Action action = agent.agent_start(initObs);
        Reward_observation_terminal rewObs = null;

        // return the cumulative reward (return) for that episode
        double reward = 0;
        
        // Perform steps until the episode is over or the state is terminal
        for (int timestep = 0; timestep < EPISODE_LENGTH; timestep++) {
            rewObs = predictor.env_step(action);
            reward += rewObs.getReward();
            if (rewObs.isTerminal()) {
                break;
            }
            action = agent.agent_step(rewObs.getReward(), rewObs.getObservation());
        }
        agent.agent_end(rewObs.getReward());
        
        return reward;
    }
}
