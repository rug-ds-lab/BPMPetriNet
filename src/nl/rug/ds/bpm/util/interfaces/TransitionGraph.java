package nl.rug.ds.bpm.util.interfaces;

import nl.rug.ds.bpm.util.interfaces.element.T;
import nl.rug.ds.bpm.util.interfaces.marking.M;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface TransitionGraph {
	M getInitialMarking();
	
	boolean isEnabled(T transition, M marking);
	boolean isParallelEnabled(Set<? extends T> ts, M m);

	Collection<? extends T> getEnabledTransitions(M marking);
	Set<? extends Set<? extends T>> getParallelEnabledTransitions(M marking);
	
	Set<? extends M> fireTransition(T transition, M marking);
	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
}
