package nl.rug.ds.bpm.pnml.jaxb.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "PetriNetDoc")
public class Pnml {
	private Set<Net> nets;

	public Pnml() {
		nets = new HashSet<>();
	}
	
	@XmlElement(name = "net", required = true)
	public Set<Net> getNets() {
		return nets;
	}
	
	public void setNets(Set<Net> nets) {
		this.nets = nets;
	}
}
