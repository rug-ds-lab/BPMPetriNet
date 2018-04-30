package nl.rug.ds.bpm.pnml.jaxb.core;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.Inscription;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "arc")
public class Arc extends PNObject {
	private String source;
	private String target;

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
}
