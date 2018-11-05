package nl.rug.ds.bpm.petrinet.interfaces.unfolding;

import nl.rug.ds.bpm.petrinet.interfaces.element.P;
import nl.rug.ds.bpm.petrinet.interfaces.element.T;
import nl.rug.ds.bpm.petrinet.interfaces.graph.TransitionGraph;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.util.Collection;

/**
 * Created by Nick van Beest on 1 Jun. 2018
 *
 */
public interface UnfoldableNet extends TransitionGraph {
	Collection<? extends T> getTransitions();
	Collection<? extends P> getSinks();
	Collection<? extends T> getPreSet(P p);

	P addPlace(String id) throws MalformedNetException;
	T addTransition(String id) throws MalformedNetException;
	void addNext(String sourceId, String targetId) throws MalformedNetException;
}
