package nl.rug.ds.bpm.pnml.jaxb.core;

import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;
import nl.rug.ds.bpm.pnml.jaxb.core.node.place.Place;
import nl.rug.ds.bpm.pnml.jaxb.core.node.place.RefPlace;
import nl.rug.ds.bpm.pnml.jaxb.core.node.transition.RefTransition;
import nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "page")
public class Page extends PNObject {
	private Set<Place> places;
	private Set<RefPlace> refPlaces;
	private Set<Transition> transitions;
	private Set<RefTransition> refTransitions;
	private Set<Arc> arcs;
	private Set<Page> pages;
	
	
	public Page() {
		places = new HashSet<>();
		refPlaces = new HashSet<>();
		transitions = new HashSet<>();
		refTransitions = new HashSet<>();
		arcs = new HashSet<>();
		pages = new HashSet<>();
	}
	
	public Page(String id) {
		super(id);
		places = new HashSet<>();
		refPlaces = new HashSet<>();
		transitions = new HashSet<>();
		refTransitions = new HashSet<>();
		arcs = new HashSet<>();
		pages = new HashSet<>();
	}
	
	public Page(String id, String name) {
		super(id, name);
		places = new HashSet<>();
		refPlaces = new HashSet<>();
		transitions = new HashSet<>();
		refTransitions = new HashSet<>();
		arcs = new HashSet<>();
		pages = new HashSet<>();
	}
	
	@XmlElement(name = "place")
	public Set<Place> getPlaces() {
		return places;
	}
	
	public void setPlaces(Set<Place> places) {
		this.places = places;
	}
	
	@XmlElement(name = "referencePlace")
	public Set<RefPlace> getRefPlaces() {
		return refPlaces;
	}
	
	public void setRefPlaces(Set<RefPlace> refPlaces) {
		this.refPlaces = refPlaces;
	}
	
	@XmlElement(name = "transition")
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public void setTransitions(Set<Transition> transitions)
	{
		this.transitions = transitions;
	}
	
	@XmlElement(name = "referenceTransition")
	public Set<RefTransition> getRefTransitions() {
		return refTransitions;
	}
	
	public void setRefTransitions(Set<RefTransition> refTransitions) {
		this.refTransitions = refTransitions;
	}
	
	@XmlElement(name = "arc")
	public Set<Arc> getArcs() {	return arcs; }
	
	public void setArcs(Set<Arc> arcs) {
		this.arcs = arcs;
	}

	@XmlElement(name = "page")
	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}
}
