package nl.rug.ds.bpm.pnml.jaxb.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "PetriNetDoc")
public class Pnml {
	private List<Net> nets;

	public Pnml() {
		nets = new ArrayList<>();
	}
	
	@XmlElement(name = "net", required = true)
	public List<Net> getNets() {
		return nets;
	}
	
	public void setNets(List<Net> nets) {
		this.nets = nets;
	}
}
