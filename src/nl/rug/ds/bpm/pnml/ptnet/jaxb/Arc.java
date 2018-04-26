package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Inscription;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "arc")
public class Arc {
	private String id;
	private String source;
	private String target;
	private Inscription weight;

	public Arc(String id, String source, String target) {
		this.id = id;
		this.source = source;
		this.target = target;
	}

	public Arc(String id, String source, String target, int weight) {
		this(id, source, target);
		this.weight = new Inscription("" + weight);
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
