package nl.rug.ds.bpm.pnml.jaxb.ptnet.node.transition;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Node;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class TransitionNode extends Node {
	public TransitionNode() { super(); }
	
	public TransitionNode(String id) {
		super(id);
	}

	public TransitionNode(String id, String name) {
		super(id, name);
	}
}
