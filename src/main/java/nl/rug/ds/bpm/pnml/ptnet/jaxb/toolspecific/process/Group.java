package nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "group")
public class Group {
	private String id;
	private String name;
	
	public Group() { }

	public Group(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
