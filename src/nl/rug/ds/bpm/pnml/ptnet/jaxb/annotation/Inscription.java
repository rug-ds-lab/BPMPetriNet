package nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.Annotation;

import javax.xml.bind.annotation.XmlRootElement;

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
