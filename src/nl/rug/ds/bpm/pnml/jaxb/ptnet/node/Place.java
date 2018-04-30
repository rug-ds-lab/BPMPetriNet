package nl.rug.ds.bpm.pnml.jaxb.ptnet.node;

import nl.rug.ds.bpm.pnml.jaxb.core.node.place.PlaceNode;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.InitialMarking;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public class Place extends nl.rug.ds.bpm.pnml.jaxb.core.node.place.Place {
	private InitialMarking initialMarking;

	public Place(String id) {
		super(id);
	}

	public Place(String id, String name) {
		super(id, name);
	}

	public InitialMarking getInitialMarking() {
		return initialMarking;
	}

	public void setInitialMarking(InitialMarking initialMarking) {
		this.initialMarking = initialMarking;
	}
}
