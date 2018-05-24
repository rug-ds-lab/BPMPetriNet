package nl.rug.ds.bpm.net;

import nl.rug.ds.bpm.net.element.T;
import nl.rug.ds.bpm.net.marking.DataM;
import nl.rug.ds.bpm.net.marking.M;

import java.util.Set;

/**
 * Created by Heerko Groefsema on 18-May-18.
 */
public interface DataDrivenGraph extends TransitionGraph {
	DataM getInitialMarking();

	Set<? extends DataM> fireTransition(T transition, M marking);
	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
}
