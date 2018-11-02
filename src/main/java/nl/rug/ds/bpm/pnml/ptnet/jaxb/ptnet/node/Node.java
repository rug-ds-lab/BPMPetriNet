package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.NetObject;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class Node extends NetObject {

	public Node() {
		super();
	}

	public Node(String id) {
		super(id);
	}

	public Node(String id, String name) {
		super(id, name);
	}
}
