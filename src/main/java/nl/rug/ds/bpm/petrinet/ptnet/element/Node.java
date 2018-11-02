package nl.rug.ds.bpm.petrinet.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Name;

public abstract class Node {
	protected nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.Node xmlElement;

	public Node() {}
	
	public Node(nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.Node xmlElement) {
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

	public nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.Node getXmlElement() {
		return xmlElement;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    
	    return (this.hashCode() == obj.hashCode());
	}
	
	@Override
	public String toString() {
		return getId() + "_" + getName();
	}
}
