package nl.rug.ds.bpm.pnml.jaxb.ptnet.node.place;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public class Place extends PlaceNode {
	public Place() { super(); }
	
	public Place(String id) { super(id); }

	public Place(String id, String name) {
		super(id, name);
	}
}
