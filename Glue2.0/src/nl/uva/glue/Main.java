package nl.uva.glue;

import nl.uva.agent.RandomAgent;

import org.rlcommunity.environment.helicopter.generalized.GeneralizedHelicopter;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.RLGlue;

import rlVizLib.Environments.EnvironmentBase;

public class Main {

	public static void main(final String[] args) {
		AgentInterface agent = new RandomAgent();
		EnvironmentBase environment = new GeneralizedHelicopter();

		int whichTrainingMDP = 1; // select the MDP to load

		// Uncomment ONE of the following lines to choose your experiment
		// consoleTrainerHelper.loadTetris(whichTrainingMDP); //whichTrainingMDP
		// should be in [0,19]
		consoleTrainerHelper.loadHelicopter(whichTrainingMDP); // whichTrainingMDP
																// should be in
																// [0,9]
		// consoleTrainerHelper.loadPolyathlon(whichTrainingMDP);
		// //whichTrainingMDP should be in [0,5]
		// consoleTrainerHelper.loadOctopus();
		// consoleTrainerHelper.loadAcrobot(whichTrainingMDP);//whichTrainingMDP
		// should be in [0,39] //0 is standard acrobot
		// consoleTrainerHelper.loadMario(0,0,0,0);

		RLGlue.RL_init();

		int episodeCount = 10; // number of episodes to run
		int maxEpisodeLength = 20000; // set a maxEpisodeLength to cut off long
										// episodes

		int totalSteps = 0;// counter for the total number of steps taken to
							// finish all episodes
		// run the episodes with RL_episode(maxEpisodeLength)
		for (int i = 0; i < episodeCount; i++) {
			RLGlue.RL_episode(maxEpisodeLength);
			totalSteps += RLGlue.RL_num_steps();
			System.out.println("Episode: " + i + " steps: "
					+ RLGlue.RL_num_steps());
		}

		System.out.println("totalSteps is: " + totalSteps);

		// clean up the environment and end the program
		RLGlue.RL_cleanup();
	}

}
