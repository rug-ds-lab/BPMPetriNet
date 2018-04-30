package nl.rug.ds.bpm.pnml.jaxb.core.node.place;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.InitialMarking;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public class Place extends PlaceNode {
	public Place(String id) {
		super(id);
	}

	public Place(String id, String name) {
		super(id, name);
	}
}
