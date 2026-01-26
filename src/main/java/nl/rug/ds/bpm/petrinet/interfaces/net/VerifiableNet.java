package nl.rug.ds.bpm.petrinet.interfaces.net;

import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 14-May-18.
 * Interface that exposes functionality required for the BPM Verification package
 */
public interface VerifiableNet {

	String getId();
	String getName();

	MarkingI getInitialMarking();
	
	boolean isEnabled(TransitionI transition, MarkingI marking);
	boolean isParallelEnabled(Set<? extends TransitionI> ts, MarkingI m);

	Collection<? extends TransitionI> getEnabledTransitions(MarkingI marking);
	Set<? extends Set<? extends TransitionI>> getParallelEnabledTransitions(MarkingI marking);

	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
	Set<? extends MarkingI> fireTransition(TransitionI transition, MarkingI marking);


}
