package nl.rug.ds.bpm.pnml.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Inscription;

public class Arc {
	private String id;
	private Node source;
	private Node target;
	private int weight;
	private nl.rug.ds.bpm.pnml.ptnet.jaxb.Arc xmlElement;

	public Arc(String id, Place source, Transition target) {
		this.id = id;
		this.source = source;
		this.target = target;
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.Arc(id, source.getId(), target.getId());
	}

	public Arc(String id, Place source, Transition target, int weight) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.weight = weight;
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.Arc(id, source.getId(), target.getId(), weight);
	}

	public Arc(nl.rug.ds.bpm.pnml.ptnet.jaxb.Arc xmlElement) {
		this.xmlElement = xmlElement;

		try {
			weight = Integer.parseInt(xmlElement.getWeight().getText().getText());
		} catch (Exception e) {}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		xmlElement.setId(id);
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
		this.weight = weight;
		xmlElement.setWeight(new Inscription("" + weight));
	}

	public nl.rug.ds.bpm.pnml.ptnet.jaxb.Arc getXmlElement() {
		return xmlElement;
	}
}
