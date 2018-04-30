package nl.rug.ds.bpm.pnml.jaxb.core.node.transition;

import nl.rug.ds.bpm.pnml.jaxb.core.node.Node;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class TransitionNode extends Node {
	public TransitionNode(String id) {
		super(id);
	}

	public TransitionNode(String id, String name) {
		super(id, name);
	}
}
