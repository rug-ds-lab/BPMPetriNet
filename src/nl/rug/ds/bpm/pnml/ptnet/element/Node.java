package nl.rug.ds.bpm.pnml.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Name;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
	protected List<Node> preSet;
	protected List<Node> postSet;

	protected String id;
	protected String name;
	protected nl.rug.ds.bpm.pnml.ptnet.jaxb.Node xmlElement;

	public Node() {
		preSet = new ArrayList<>();
		postSet = new ArrayList<>();
	}

	public Node(nl.rug.ds.bpm.pnml.ptnet.jaxb.Node xmlElement) {
		this.xmlElement = xmlElement;
		this.id = xmlElement.getId();

		try {
			this.name = xmlElement.getName().getText().getText();
		}
		catch (NullPointerException e) {}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		Name n = new Name(name);
		xmlElement.setName(n);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		xmlElement.setId(id);
	}


	public nl.rug.ds.bpm.pnml.ptnet.jaxb.Node getXmlElement() {
		return xmlElement;
	}

	public List<Node> getPreSet() {
		return preSet;
	}

	public List<Node> getPostSet() {
		return postSet;
	}

	public void addNext(Node n) {
		postSet.add(n);
	}

	public void removeNext(Node n) {
		postSet.remove(n);
	}

	public void addPrevious(Node n) {
		preSet.add(n);
	}

	public void removePrevious(Node n) {
		preSet.remove(n);
	}
}
