package nl.rug.ds.bpm.petrinet.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.InitialMarking;
import nl.rug.ds.bpm.util.interfaces.element.P;

public class Place extends Node implements P {

	public Place(String id) {
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place(id);
	}

	public Place(String id, String name) {
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place(id, name);
	}

	public Place(nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.PlaceNode xmlElement) {
		super(xmlElement);

	}

	public int getTokens() {
		int tokens = 0;
		
		try {
			tokens = Integer.parseInt(((nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place)xmlElement).getInitialMarking().getText().getText());
		}
		catch (Exception e) {}
		
		return tokens;
	}

	public void setTokens(int tokens) {
		try {
			if (tokens <= 0)
				((nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place) xmlElement).setInitialMarking(null);
			else
				((nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place) xmlElement).setInitialMarking(new InitialMarking("" + tokens));
		}
		catch (Exception e) {}
	}
}
