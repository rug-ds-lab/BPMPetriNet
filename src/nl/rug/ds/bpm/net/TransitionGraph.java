package nl.rug.ds.bpm.net;

import nl.rug.ds.bpm.net.marking.M;
import nl.rug.ds.bpm.net.element.P;
import nl.rug.ds.bpm.net.element.T;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface TransitionGraph {
	M getInitialMarking();
	
	Collection<? extends T> getEnabledTransitions(M marking);
	Set<? extends Set<? extends T>> getParallelEnabledTransitions(M marking);
	
	Set<? extends M> fireTransition(T transition, M marking);
	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
}
