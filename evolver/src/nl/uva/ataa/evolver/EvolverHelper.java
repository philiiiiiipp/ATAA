package nl.uva.ataa.evolver;

public class EvolverHelper {

    public static double[] adjustFitness(final double[] fitness) {
        final double[] adjustedFitness = new double[fitness.length];

        // Find the worst fitness
        double worstFitness = Double.MAX_VALUE;
        for (final double fitnessValue : fitness) {
            if (fitnessValue < worstFitness) {
                worstFitness = fitnessValue;
            }
        }

        // Adjust the fitness based on the worst
        for (int i = 0; i < fitness.length; ++i) {
            adjustedFitness[i] = fitness[i] - worstFitness;
        }

        return adjustedFitness;
    }
}
