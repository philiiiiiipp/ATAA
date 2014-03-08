package nl.uva.ataa.evolver;

import nl.uva.ataa.environment.BetaPredictor;
import nl.uva.ataa.environment.betapredictors.RewardBetaPredictor;
import nl.uva.ataa.environment.betapredictors.VarianceBetaPredictor;
import nl.uva.ataa.utilities.Conf;

public class PredictorEvolver extends Evolver<BetaPredictor> {

    /**
     * The possible types of BetaPredictors to evolve.
     */
    public enum Type {
        REWARD,
        VARIANCE;
    }

    /** The chance that two chromosomes will mate */
    private static final double CROSSOVER_RATE = 0.8;
    /** The ratio between parents' genes during mating */
    private static final double CROSSOVER_RATIO = 0.8;

    /** The chance that a chromosome will mutate one of its genes */
    private static final double MUTATION_RATE = 0.8;
    /** The range that a gene can maximaly mutate */
    private static final double MUTATION_RANGE = 0.2;

    /** The amount of chromosomes that compete for reproduction */
    private static final int TOURNAMENT_SIZE = 5;
    /** How many best chromosomes will be directly transferred to the next generation [in %] */
    private static final double ELITISM_RATE = 0.01;

    /**
     * Creates an evolver with random predictors.
     * 
     * @param poolSize
     *            The amount of predictors to evolve
     * @param type
     *            The type of predictor to test
     */
    public PredictorEvolver(final int poolSize, final Type type) {
        super(poolSize, CROSSOVER_RATE, CROSSOVER_RATIO, MUTATION_RATE, MUTATION_RANGE, TOURNAMENT_SIZE, ELITISM_RATE,
                new Conf("type", type));
    }

    @Override
    protected BetaPredictor getSpecimen(final Conf conf) {
        final Type type = (Type) conf.get("type");
        switch (type) {
            case REWARD:
                return new RewardBetaPredictor();
            case VARIANCE:
                return new VarianceBetaPredictor();
            default:
                throw new RuntimeException("Unknown predictor type: " + type);
        }
    }

}
