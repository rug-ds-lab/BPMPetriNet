package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Name;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public abstract class NetObject {
	private String id;
	private Name name;
	private Graphics graphics;
	private Set<ToolSpecific> toolSpecifics;
	
	public NetObject() {
		toolSpecifics = new HashSet<>();
	}
	
	public NetObject(String id) {
		this();
		this.id = id;
	}
	
	public NetObject(String id, String name) {
		this();
		this.id = id;
		this.name = new Name(name);
	}
	
	@XmlAttribute
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public Name getName() {
		return name;
	}
	
	public void setName(Name name) {
		this.name = name;
	}
	
	@XmlElement(name = "graphics")
	public Graphics getGraphics() {
		return graphics;
	}
	
	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}
	
	@XmlElement(name = "toolspecific")
	public Set<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}
	
	public void setToolSpecifics(Set<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
	}
}
