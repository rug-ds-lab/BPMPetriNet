package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "text")
public class Text {
	private String text;
	
	public Text() { }

	public Text(String text) {
		this.text = text;
	}
	
	@XmlValue
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
