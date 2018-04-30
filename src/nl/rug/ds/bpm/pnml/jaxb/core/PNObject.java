package nl.rug.ds.bpm.pnml.jaxb.core;

import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public abstract class PNObject {
	private String id;
	private Name name;
	private Graphics graphics;
	private List<ToolSpecific> toolSpecifics;
	
	public PNObject() {
		toolSpecifics = new ArrayList<>();
	}
	
	public PNObject(String id) {
		this();
		this.id = id;
	}
	
	public PNObject(String id, String name) {
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
	public List<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}
	
	public void setToolSpecifics(List<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
	}
}
