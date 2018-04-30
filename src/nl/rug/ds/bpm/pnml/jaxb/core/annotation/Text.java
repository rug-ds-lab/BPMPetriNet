package nl.rug.ds.bpm.pnml.jaxb.core.annotation;


import nl.rug.ds.bpm.pnml.jaxb.core.Graphics;
import nl.rug.ds.bpm.pnml.jaxb.core.ToolSpecific;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "text")
public class Text {
	private String text;
	private Graphics graphics;
	private List<ToolSpecific> toolSpecifics;
	
	public Text() {
		toolSpecifics = new ArrayList<>();
	}

	public Text(String text) {
		this();
		this.text = text;
	}
	
	@XmlValue
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
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
	public List<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}

	public void setToolSpecifics(List<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
	}
}
