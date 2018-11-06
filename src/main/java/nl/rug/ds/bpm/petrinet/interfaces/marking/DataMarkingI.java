package nl.rug.ds.bpm.petrinet.interfaces.marking;

import javax.script.Bindings;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface DataMarkingI extends MarkingI {
	Bindings getBindings();
	void setBindings(Bindings bindings);
}
