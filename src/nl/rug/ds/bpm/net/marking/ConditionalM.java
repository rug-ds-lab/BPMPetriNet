package nl.rug.ds.bpm.net.marking;

import java.util.Collection;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface ConditionalM extends M {
	Collection<String> getConditions();
}
