package nl.rug.ds.bpm.pnml.jaxb.ptnet;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.Inscription;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "arc")
public class Arc extends NetObject {
	private String source;
	private String target;
	private Inscription weight;
	
	public Arc() {}

	public Arc(String id, String source, String target) {
		super(id);
		this.source = source;
		this.target = target;
	}
	
	public Arc(String id, String name, String source, String target) {
		super(id, name);
		this.source = source;
		this.target = target;
	}
	
	public Arc(String id, String source, String target, int weight) {
		this(id, source, target);
		this.weight = new Inscription("" + weight);
	}
	
	public Arc(String id, String name, String source, String target, int weight) {
		this(id, name, source, target);
		this.weight = new Inscription("" + weight);
	}

	@XmlAttribute(name = "source", required = true)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@XmlAttribute(name = "target", required = true)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@XmlElement(name = "inscription")
	public Inscription getWeight() {
		return weight;
	}
	
	public void setWeight(Inscription weight) {
		this.weight = weight;
	}
}
