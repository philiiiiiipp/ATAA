package nl.uva.ataa.agent.genetic;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class EvaluateModel extends FitnessFunction {

	/**
	 * Generated default serial UID
	 */
	private static final long serialVersionUID = 3905984536032544547L;

	public EvaluateModel() {

	}

	@Override
	protected double evaluate(final IChromosome a_subject) {

		SuperGene[] genes = new SuperGene[a_subject.getGenes().length];
		for (int i = 0; i < a_subject.getGenes().length; ++i) {
			genes[i] = (SuperGene) a_subject.getGene(i);

		}

		return 1;
	}
}
