package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public class Transition extends TransitionNode {
	public Transition() { super(); }
	
	public Transition(String id) {
		super(id);
	}

	public Transition(String id, String name) {
		super(id, name);
	}
}
