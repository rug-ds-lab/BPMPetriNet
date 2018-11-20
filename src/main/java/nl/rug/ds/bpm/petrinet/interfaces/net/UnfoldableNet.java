package nl.rug.ds.bpm.petrinet.interfaces.net;

import nl.rug.ds.bpm.petrinet.interfaces.element.ArcI;
import nl.rug.ds.bpm.petrinet.interfaces.element.PlaceI;
import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Nick van Beest on 1 Jun. 2018
 *
 */
public interface UnfoldableNet extends VerifiableNet {
	Collection<? extends TransitionI> getTransitions();
	Collection<? extends PlaceI> getSinks();
	Collection<? extends TransitionI> getPreSet(PlaceI p);

	PlaceI addPlace(String id) throws MalformedNetException;
	TransitionI addTransition(String id) throws MalformedNetException;
	ArcI addArc(String sourceId, String targetId) throws MalformedNetException;
	
	boolean isParallelEnabled(Set<? extends TransitionI> ts, MarkingI m, boolean ignoreGuardConflicts);
	Set<? extends Set<? extends TransitionI>> getParallelEnabledTransitions(MarkingI marking, boolean ignoreGuardConflicts);

}
