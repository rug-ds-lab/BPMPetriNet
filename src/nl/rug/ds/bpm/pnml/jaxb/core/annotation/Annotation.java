package nl.rug.ds.bpm.pnml.jaxb.core.annotation;

import nl.rug.ds.bpm.pnml.jaxb.core.Graphics;
import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Text;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

public abstract class Annotation {
	private Text text;
	private Graphics graphics;
	
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
}
