package nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation;


import javax.xml.bind.annotation.XmlAnyElement;
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
	private List<Object> any;
	
	public Text() {
		any = new ArrayList<>();
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
	
	@XmlAnyElement(lax = false)
	public List<Object> getAny() {
		return any;
	}
	
	public void setAny(List<Object> any) {
		this.any = any;
	}
}
