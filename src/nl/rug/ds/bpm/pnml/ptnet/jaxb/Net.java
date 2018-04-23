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
	private final String type = "http://www.pnml.org/version-2009/grammar/ptnet";
	private List<Page> pages;
	
	public Net() {
		pages = new ArrayList<>();
	}
	
	public Net(String id) {
		this();
		this.id = id;
	}
	
	@XmlAttribute
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	@XmlElement(name = "page")
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
}
