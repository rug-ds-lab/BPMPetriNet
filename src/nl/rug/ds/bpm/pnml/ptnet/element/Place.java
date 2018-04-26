package nl.rug.ds.bpm.pnml.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.InitialMarking;

public class Place extends Node {
	private int tokens;

	public Place(String id) {
		super();
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.Place(id);
		this.id = id;
		tokens = 0;
	}

	public Place(String id, String name) {
		super();
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.Place(id, name);
		this.id = id;
		this.name = name;
		tokens = 0;
	}

	public Place(nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.Place xmlElement) {
		super(xmlElement);

		try {
			tokens = Integer.parseInt(xmlElement.getInitialMarking().getText().getText());
		}
		catch (NullPointerException e) {}
	}

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
		InitialMarking i = new InitialMarking("" + tokens);
		((nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.Place) xmlElement).setInitialMarking(i);
	}

	public boolean isSink() {
		return postSet.isEmpty();
	}

	public boolean isSource() {
		return preSet.isEmpty();
	}
}
