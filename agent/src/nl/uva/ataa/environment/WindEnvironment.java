package nl.uva.ataa.environment;

import java.util.Random;

import org.rlcommunity.environment.helicopter.Helicopter;

public class WindEnvironment extends Helicopter {

    /**
     * Creates an environment with random wind settings
     */
    public WindEnvironment() {
        final Random rand = new Random();
        setWindWaveNS(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        setWindWaveEW(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    /**
     * Creates an environment with the given wind settings
     * 
     * @param windNS
     *            Wind for the North-South-axis
     * @param windEW
     *            Wind for the East-West-axis
     */
    public WindEnvironment(final Wind windNS, final Wind windEW) {
        setWindWaveNS(windNS.getMaxStr(), windNS.getHz(), windNS.getPhase(), windNS.getCenterAmp());
        setWindWaveEW(windEW.getMaxStr(), windEW.getHz(), windEW.getPhase(), windEW.getCenterAmp());
    }
}
