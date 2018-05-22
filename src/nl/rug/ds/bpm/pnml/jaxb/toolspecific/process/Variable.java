package nl.rug.ds.bpm.pnml.jaxb.toolspecific.process;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "variable")
public class Variable {
	private String value;
	private String type;
	private String name;
	
	public Variable() { }

	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
		value = "";
	}

	public Variable(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
