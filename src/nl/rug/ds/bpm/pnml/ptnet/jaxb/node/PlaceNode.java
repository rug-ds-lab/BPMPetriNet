package nl.rug.ds.bpm.pnml.ptnet.jaxb.node;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.Node;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class PlaceNode extends Node {
	public PlaceNode(String id) {
		super(id);
	}

	public PlaceNode(String id, String name) {
		super(id, name);
	}
}
