package nl.rug.ds.bpm.pnml.petrinet;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.Inscription;
import nl.rug.ds.bpm.pnml.petrinet.node.Node;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public class Arc {
	private Node source;
	private Node target;
	private nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc xmlElement;
	
	public Arc(String id, Node source, Node target) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc(id, source.getId(), target.getId());
		this.source = source;
		this.target = target;
	}
	
	public Arc(nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc xmlElement) {
		this.xmlElement = xmlElement;
	}
	
	public nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc getXmlElement() {
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
		int weight = 1;
		
		try {
			weight = Integer.parseInt(xmlElement.getWeight().getText().getText());
		} catch (Exception e) {}
		
		return weight;
	}
	
	public void setWeight(int weight) {
		xmlElement.setWeight(new Inscription("" + weight));
	}
}
