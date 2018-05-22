package nl.rug.ds.bpm.net.marking;

import javax.script.Bindings;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface DataM extends M {
	Bindings getBindings();
	void setBindings(Bindings bindings);
}
