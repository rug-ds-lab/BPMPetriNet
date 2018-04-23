package nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.Annotation;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "name")
public class Name extends Annotation {
	
	public Name() {}
	
	public Name(String name) {
		super(name);
	}
}
