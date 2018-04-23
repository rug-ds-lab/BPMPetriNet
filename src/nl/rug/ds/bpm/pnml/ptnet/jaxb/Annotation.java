package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Text;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

public class Annotation {
	private Text text;
	private List<Object> any;
	
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
	
	@XmlAnyElement(lax = false)
	public List<Object> getAny() {
		return any;
	}
	
	public void setAny(List<Object> any) {
		this.any = any;
	}
}
