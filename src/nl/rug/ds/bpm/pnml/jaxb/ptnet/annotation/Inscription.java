package nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation;

import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Annotation;

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
