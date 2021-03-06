package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "referencePlace")
public class RefPlace extends PlaceNode {
	private String ref;
	
	public RefPlace() { super(); }
	
	public RefPlace(String id, String ref) {
		super(id);
		this.ref = ref;
	}

	@XmlAttribute(name = "ref", required = true)
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
}
