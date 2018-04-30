package nl.rug.ds.bpm.pnml.petrinet.node;

import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;

public abstract class Node {
	protected nl.rug.ds.bpm.pnml.jaxb.core.node.Node xmlElement;

	public Node() {}
	
	public Node(nl.rug.ds.bpm.pnml.jaxb.core.node.Node xmlElement) {
		this.xmlElement = xmlElement;
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
