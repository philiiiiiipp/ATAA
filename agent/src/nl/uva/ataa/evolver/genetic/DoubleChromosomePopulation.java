package nl.uva.ataa.evolver.genetic;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ListPopulation;
import org.apache.commons.math3.genetics.Population;

public class DoubleChromosomePopulation extends ListPopulation {

    final List<DoubleChromosome> mDoubleChromosomes = new LinkedList<>();

    public DoubleChromosomePopulation(final int populationLimit) throws NotPositiveException {
        super(populationLimit);
    }

    @Override
    public void addChromosome(final Chromosome chromosome) throws NumberIsTooLargeException {
        mDoubleChromosomes.add((DoubleChromosome) chromosome);
        super.addChromosome(chromosome);
    }

    @Override
    public Population nextGeneration() {
        return new DoubleChromosomePopulation(getPopulationLimit());
    }

    public List<DoubleChromosome> getDoubleChromosomes() {
        return mDoubleChromosomes;
    }

}
