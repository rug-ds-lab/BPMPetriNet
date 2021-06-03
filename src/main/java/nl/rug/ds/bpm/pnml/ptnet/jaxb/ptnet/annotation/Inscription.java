package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */
@XmlRootElement(name = "inscription")
public class Inscription extends Annotation {

	public Inscription() {}

	public Inscription(String text) {
		super(text);
	}
}
