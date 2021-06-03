package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "initialMarking")
public class InitialMarking extends Annotation {

	public InitialMarking() {}

	public InitialMarking(String text) {
		super(text);
	}
}
