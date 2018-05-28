package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "graphics")
public class Graphics {
	private List<Object> graphics;
	private List<ToolSpecific> toolSpecifics;

	public Graphics() {
		toolSpecifics = new ArrayList<>();
		graphics = new ArrayList<>();
	}

	@XmlElement(name = "toolspecific")
	public List<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}

	public void setToolSpecifics(List<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
	}

	@XmlAnyElement(lax = false)
	public List<Object> getGraphics() {
		return graphics;
	}
}
