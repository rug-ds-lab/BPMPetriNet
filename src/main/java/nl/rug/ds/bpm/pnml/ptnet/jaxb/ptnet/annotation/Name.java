package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation;

import jakarta.xml.bind.annotation.XmlRootElement;

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
