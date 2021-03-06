package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Graphics;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.ToolSpecific;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

public abstract class Annotation {
	private Text text;
	private Graphics graphics;
	private Set<ToolSpecific> toolSpecifics;
	
	public Annotation() {}
	
	public Annotation(String text) {
		this.text = new Text(text);
	}
	
	@XmlElement(name = "text")
	public Text getText() {
		return text;
	}
	
	public void setText(Text text) {
		this.text = text;
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
