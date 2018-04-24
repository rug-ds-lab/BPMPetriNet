package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Name;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class Node {
	private String id;
	private Name name;
	private Graphics graphics;
	private List<ToolSpecific> toolSpecifics;

	public Node() {
		toolSpecifics = new ArrayList<>();
	}

	public Node(String id) {
		this();
		this.id = id;
	}

	public Node(String id, String name) {
		this(id);
		this.name = new Name(name);
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@XmlElement(name = "name")
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}
}
