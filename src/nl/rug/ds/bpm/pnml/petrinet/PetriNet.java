package nl.rug.ds.bpm.pnml.petrinet;

import nl.rug.ds.bpm.pnml.jaxb.core.Net;
import nl.rug.ds.bpm.pnml.jaxb.core.NetContainer;
import nl.rug.ds.bpm.pnml.jaxb.core.ToolSpecific;
import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.Process;
import nl.rug.ds.bpm.pnml.petrinet.element.Arc;
import nl.rug.ds.bpm.pnml.petrinet.element.Node;
import nl.rug.ds.bpm.pnml.petrinet.element.Place;
import nl.rug.ds.bpm.pnml.petrinet.element.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PetriNet {
	private HashMap<String, Node> nodes;
	private HashMap<String, Place> places;
	private HashMap<String, Transition> transitions;

	private HashMap<String, Arc> arcs;
	private HashMap<String, Set<Arc>> incoming;
	private HashMap<String, Set<Arc>> outgoing;

	private Set<PetriNet> pages;

	private Process process;
	private NetContainer xmlElement;

	public PetriNet() {
		nodes = new HashMap<>();
		places = new HashMap<>();
		transitions = new HashMap<>();

		arcs = new HashMap<>();
		incoming = new HashMap<>();
		outgoing = new HashMap<>();

		pages = new HashSet<>();

		process = new Process();
		ToolSpecific toolSpecific = new ToolSpecific();
		toolSpecific.setProcess(process);
		xmlElement = new Net();
		xmlElement.getToolSpecifics().add(toolSpecific);
	}

	public PetriNet(String id) {
		this();
		xmlElement.setId(id);
	}

	public PetriNet(String id, String name) {
		this();
		xmlElement.setId(id);
		xmlElement.setName(new Name(name));
	}

	public PetriNet(NetContainer xmlElement) {
		nodes = new HashMap<>();
		places = new HashMap<>();
		transitions = new HashMap<>();

		arcs = new HashMap<>();
		incoming = new HashMap<>();
		outgoing = new HashMap<>();

		pages = new HashSet<>();

		this.xmlElement = xmlElement;

		for (nl.rug.ds.bpm.pnml.jaxb.core.node.place.Place place: xmlElement.getPlaces()) {
			Place p = new Place(place);
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.jaxb.core.node.place.RefPlace place: xmlElement.getRefPlaces()) {
			Place p = new Place(place);
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition transition: xmlElement.getTransitions()) {
			Transition t = new Transition(transition);
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.jaxb.core.node.transition.RefTransition transition: xmlElement.getRefTransitions()){
			Transition t = new Transition(transition);
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.jaxb.core.Arc arc: xmlElement.getArcs()) {
			Arc a = new Arc((nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc)arc);
			a.setSource(nodes.get(arc.getSource()));
			a.setTarget(nodes.get(arc.getTarget()));
			arcs.put(arc.getId(), a);
			incoming.get(arc.getTarget()).add(a);
			outgoing.get(arc.getSource()).add(a);
		}

		for (NetContainer page: xmlElement.getPages())
			pages.add(new PetriNet(page));

		for (ToolSpecific toolSpecific: xmlElement.getToolSpecifics())
			if(toolSpecific.getTool().equals("nl.rug.ds.bpm.pnml.petrinet"))
				process = toolSpecific.getProcess();
	}

	//Transition methods
	public void addTransition(Transition t) {
		transitions.put(t.getId(), t);
		nodes.put(t.getId(), t);
		Set<Arc> in = new HashSet<>();
		Set<Arc> out = new HashSet<>();
		incoming.put(t.getId(), in);
		outgoing.put(t.getId(), out);

		xmlElement.getTransitions().add((nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition) t.getXmlElement());
	}

	public Transition addTransition(String id) {
		Transition t = new Transition(id);
		addTransition(t);
		return t;
	}

	public Transition addTransition(String id, String name) {
		Transition t = new Transition(id, name);
		addTransition(t);
		return t;
	}

	public void removeTransition(Transition t) {
		transitions.remove(t.getId());
		nodes.remove(t.getId());

		removeArcs(t);

		xmlElement.getTransitions().remove(t.getXmlElement());
		xmlElement.getRefTransitions().remove(t.getXmlElement());
	}

	public void removeTransition(String id) {
		if(transitions.containsKey(id))
			removeTransition(transitions.get(id));
	}

	public Transition getTransition(String id) {
		return transitions.get(id);
	}

	public Set<Transition> getTransitions() {
		return (Set<Transition>) transitions.values();
	}

	//Place methods
	public void addPlace(Place p) {
		places.put(p.getId(), p);
		nodes.put(p.getId(), p);

		Set<Arc> in = new HashSet<>();
		Set<Arc> out = new HashSet<>();
		incoming.put(p.getId(), in);
		outgoing.put(p.getId(), out);

		xmlElement.getPlaces().add((nl.rug.ds.bpm.pnml.jaxb.core.node.place.Place) p.getXmlElement());
	}

	public Place addPlace(String id) {
		Place p = new Place(id);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, int tokens) {
		Place p = new Place(id);
		p.setTokens(tokens);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, String name) {
		Place p = new Place(id, name);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, String name, int tokens) {
		Place p = new Place(id, name);
		p.setTokens(tokens);
		addPlace(p);
		return p;
	}

	public void removePlace(Place p) {
		places.remove(p.getId());
		nodes.remove(p.getId());

		removeArcs(p);

		xmlElement.getPlaces().remove(p.getXmlElement());
		xmlElement.getRefPlaces().remove(p.getXmlElement());
	}

	public void removePlace(String id) {
		if (places.containsKey(id))
			removePlace(places.get(id));
	}

	public Place getPlace(String id) {
		return places.get(id);
	}

	public Set<Place> getPlaces() {
		return (Set<Place>) places.values();
	}

	//Arc methods
	public void addArc(Arc a) {
		arcs.put(a.getId(), a);
		incoming.get(a.getTarget().getId()).add(a);
		outgoing.get(a.getSource().getId()).add(a);

		xmlElement.getArcs().add(a.getXmlElement());
	}

	public Arc addArc(Node source, Node target) {
		Arc a = new Arc(source.getId() + "-" + target.getId(), source, target);
		addArc(a);
		return a;
	}

	public Arc addArc(Node source, Node target, int weight) {
		Arc a = new Arc(source.getId() + "-" + target.getId(), source, target);
		a.setWeight(weight);
		addArc(a);
		return a;
	}

	public Arc addArc(String sourceId, String targetId) {
		if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId))
			return null;
		return addArc(nodes.get(sourceId), nodes.get(targetId));
	}

	public Arc addArc(String sourceId, String targetId, int weight) {
		if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId))
			return null;
		Arc a = addArc(nodes.get(sourceId), nodes.get(targetId));
		a.setWeight(weight);
		return a;
	}

	public void removeArc(Arc a) {
		arcs.remove(a.getId());
		incoming.get()
	}

	public Arc getArc(String id) {
		return arcs.get(id);
	}

	public Set<Arc> getArcs() {
		return (Set<Arc>) arcs.values();
	}
}
