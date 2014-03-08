package nl.uva.ataa.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * A general configuration used to pass arguments along through constructors when methods need values that can't be set
 * before super() calls.
 */
public class Conf {

    private final Map<Object, Object> mParameters = new HashMap<>();

    /**
     * Prepares a new config object with the specified parameters.
     * 
     * @param parameters
     *            The set of parameters in the form: key, value, key, value, ...
     */
    public Conf(final Object... parameters) {
        for (int i = 0; i < parameters.length; i += 2) {
            mParameters.put(parameters[i], parameters[i + 1]);
        }
    }

    /**
     * Retrieves the configuration value mapped with the given key.
     * 
     * @param key
     *            The key for the value
     * 
     * @return The value
     */
    public Object get(final Object key) {
        return mParameters.get(key);
    }
}