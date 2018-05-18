package nl.rug.ds.bpm.net;

import nl.rug.ds.bpm.net.element.T;
import nl.rug.ds.bpm.net.marking.DataM;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 18-May-18.
 */
public interface DataDrivenGraph {
	DataM getInitialMarking();
	
	boolean isEnabled(T transition, DataM marking);
	Collection<? extends T> getEnabledTransitions(DataM marking);
	Set<? extends Set<? extends T>> getParallelEnabledTransitions(DataM marking);
	
	Set<? extends DataM> fireTransition(T transition, DataM marking);
	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
}
