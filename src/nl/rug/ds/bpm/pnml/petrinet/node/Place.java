package nl.rug.ds.bpm.pnml.petrinet.node;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.InitialMarking;

public class Place extends Node {

	public Place(String id) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place(id);
	}

	public Place(String id, String name) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place(id, name);
	}

	public Place(nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place xmlElement) {
		super(xmlElement);

	}

	public int getTokens() {
		int tokens = 0;
		
		try {
			tokens = Integer.parseInt(((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place)xmlElement).getInitialMarking().getText().getText());
		}
		catch (NullPointerException e) {}
		
		return tokens;
	}

	public void setTokens(int tokens) {
		((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place) xmlElement).setInitialMarking(new InitialMarking("" + tokens));
	}
}
