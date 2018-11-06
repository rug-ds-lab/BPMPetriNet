package nl.rug.ds.bpm.petrinet.ptnet.element;

import nl.rug.ds.bpm.petrinet.interfaces.element.ArcI;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Inscription;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Name;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public class Arc implements ArcI {
	private Node source;
	private Node target;
	private int weight;
	private nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Arc xmlElement;
	
	public Arc(String id, Node source, Node target) {
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Arc(id, source.getId(), target.getId());
		this.source = source;
		this.target = target;
		weight = 1;
	}
	
	public Arc(nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Arc xmlElement) {
		this.xmlElement = xmlElement;
		
		try {
			weight = Integer.parseInt(xmlElement.getWeight().getText().getText());
		} catch (Exception e) {
			weight = 1;
		}
	}
	
	public nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Arc getXmlElement() {
		return xmlElement;
	}
	
	public Node getSource() {
		return source;
	}
	
	public void setSource(Node source) {
		this.source = source;
		xmlElement.setSource(source.getId());
	}
	
	public Node getTarget() {
		return target;
	}
	
	public void setTarget(Node target) {
		this.target = target;
		xmlElement.setTarget(target.getId());
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		if(weight <= 1) {
			this.weight = 1;
			xmlElement.setWeight(null);
		}
		else {
			this.weight = weight;
			xmlElement.setWeight(new Inscription("" + weight));
		}
	}

	public String getName() {
		String name = "";

		try {
			name = xmlElement.getName().getText().getText();
		}
		catch (NullPointerException e) {}

		return name;
	}

	public void setName(String name) {
		xmlElement.setName(new Name(name));
	}

	public String getId() {
		String id = "";

		try {
			id = xmlElement.getId();
		}
		catch (NullPointerException e) {}

		return id;
	}

	public void setId(String id) {
		xmlElement.setId(id);
	}
}
