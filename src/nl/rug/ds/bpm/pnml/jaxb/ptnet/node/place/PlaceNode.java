package nl.rug.ds.bpm.pnml.jaxb.ptnet.node.place;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Node;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.InitialMarking;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class PlaceNode extends Node {
	private InitialMarking initialMarking;
	
	public PlaceNode() { super(); }
	
	public PlaceNode(String id) {
		super(id);
	}

	public PlaceNode(String id, String name) {
		super(id, name);
	}
	
	public InitialMarking getInitialMarking() {
		return initialMarking;
	}
	
	public void setInitialMarking(InitialMarking initialMarking) {
		this.initialMarking = initialMarking;
	}
}
