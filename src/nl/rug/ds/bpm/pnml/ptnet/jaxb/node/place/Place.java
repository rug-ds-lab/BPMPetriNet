package nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.InitialMarking;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.node.PlaceNode;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public class Place extends PlaceNode {
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
