package nl.rug.ds.bpm.pnml.jaxb.core.node;

import nl.rug.ds.bpm.pnml.jaxb.core.Graphics;
import nl.rug.ds.bpm.pnml.jaxb.core.PNObject;
import nl.rug.ds.bpm.pnml.jaxb.core.ToolSpecific;
import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */
public abstract class Node extends PNObject {

	public Node() {
		super();
	}

	public Node(String id) {
		super(id);
	}

	public Node(String id, String name) {
		super(id, name);
	}
}
