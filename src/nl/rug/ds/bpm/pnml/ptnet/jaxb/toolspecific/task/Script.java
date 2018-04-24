package nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.task;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "script")
public class Script {
	private String type;
	private String script;

	public Script(String script, String type) {
		this.script = script;
		this.type = type;
	}

	@XmlValue
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
