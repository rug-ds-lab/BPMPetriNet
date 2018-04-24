package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "net")
public class Net {
	private String id;
	private String type = "http://www.pnml.org/version-2009/grammar/ptnet";
	private List<Page> pages;
	private Graphics graphics;
	private List<ToolSpecific> toolSpecifics;
	
	public Net() {
		pages = new ArrayList<>();
		toolSpecifics = new ArrayList<>();
	}
	
	public Net(String id) {
		this();
		this.id = id;
	}
	
	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "type", required = true)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "page", required = true)
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
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
