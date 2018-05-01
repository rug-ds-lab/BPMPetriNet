package nl.rug.ds.bpm.pnml.petrinet.element;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.InitialMarking;

public class Place extends Node {

	public Place(String id) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place(id);
	}

	public Place(String id, String name) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place(id, name);
	}

	public Place(nl.rug.ds.bpm.pnml.jaxb.core.node.place.PlaceNode xmlElement) {
		super(xmlElement);

	}

	public int getTokens() {
		int tokens = 0;
		
		try {
			tokens = Integer.parseInt(((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place)xmlElement).getInitialMarking().getText().getText());
		}
		catch (Exception e) {}
		
		return tokens;
	}

	public void setTokens(int tokens) {
		try {
			((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.Place) xmlElement).setInitialMarking(new InitialMarking("" + tokens));
		}
		catch (Exception e) {}
	}
}
