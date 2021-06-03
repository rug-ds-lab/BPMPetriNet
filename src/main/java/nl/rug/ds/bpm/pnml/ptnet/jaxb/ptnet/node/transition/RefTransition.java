package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "referenceTransition")
public class RefTransition extends TransitionNode {
	private String ref;
	
	public RefTransition() { super(); }
	
	public RefTransition(String id, String ref) {
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
