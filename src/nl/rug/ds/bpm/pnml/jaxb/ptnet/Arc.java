package nl.rug.ds.bpm.pnml.jaxb.ptnet;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.Inscription;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "arc")
public class Arc extends nl.rug.ds.bpm.pnml.jaxb.core.Arc {
	private Inscription weight;
	
	public Arc(String id, String source, String target) {
		super(id, source, target);
	}
	
	public Arc(String id, String name, String source, String target) {
		super(id, name, source, target);
	}

	public Arc(String id, String source, String target, int weight) {
		super(id, source, target);
		this.weight = new Inscription("" + weight);
	}
	
	public Arc(String id, String name, String source, String target, int weight) {
		super(id, name, source, target);
		this.weight = new Inscription("" + weight);
	}

	@XmlElement(name = "inscription")
	public Inscription getWeight() {
		return weight;
	}

	public void setWeight(Inscription weight) {
		this.weight = weight;
	}
}
