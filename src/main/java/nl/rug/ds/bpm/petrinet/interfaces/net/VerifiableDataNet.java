package nl.rug.ds.bpm.petrinet.interfaces.net;

import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.DataMarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;

import java.util.Set;

/**
 * Created by Heerko Groefsema on 18-May-18.
 */
public interface VerifiableDataNet extends VerifiableNet {
	DataMarkingI getInitialMarking();

	Set<? extends DataMarkingI> fireTransition(TransitionI transition, MarkingI marking);
	// Returns a set because nets with guards on arcs may
	// produce multiple possible future markings (e.g., CPN).
}
