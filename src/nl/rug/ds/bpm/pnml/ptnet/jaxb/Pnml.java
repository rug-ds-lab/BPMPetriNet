package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
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
	
	@XmlElement(name = "net")
	public List<Net> getNets() {
		return nets;
	}
	
	public void setNets(List<Net> nets) {
		this.nets = nets;
	}
	
	public Net getNet(String id) {
		Net net = null;
		
		Iterator<Net> pTnetIterator = nets.iterator();
		while (net == null && pTnetIterator.hasNext()) {
			Net n = pTnetIterator.next();
			if (n.getId().equals(id))
				net = n;
		}
		
		return net;
	}
	
	public boolean removeNet(Net net) {
		return nets.remove(net);
	}
	
	public boolean addNet(Net net) {
		return nets.add(net);
	}
}
