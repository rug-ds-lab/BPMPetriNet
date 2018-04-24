package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.annotation.Name;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.Place;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.node.place.RefPlace;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.node.transition.RefTransition;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.node.transition.Transition;

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
public class Page {
	private String id;
	private Name name;
	private Set<Place> places;
	private Set<RefPlace> refPlaces;
	private Set<Transition> transitions;
	private Set<RefTransition> refTransitions;
	private Set<Arc> arcs;
	private Graphics graphics;
	private List<ToolSpecific> toolSpecifics;
	
	
	public Page() {
		places = new HashSet<>();
		refPlaces = new HashSet<>();
		transitions = new HashSet<>();
		refTransitions = new HashSet<>();
		arcs = new HashSet<>();
		toolSpecifics = new ArrayList<>();
	}
	
	public Page(String id) {
		this();
		this.id = id;
	}
	
	public Page(String id, String name) {
		this();
		this.id = id;
		this.name = new Name(name);
	}
	
	@XmlAttribute
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public Name getName() {
		return name;
	}
	
	public void setName(Name name) {
		this.name = name;
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
	public Set<Arc> getArcs() {
		return arcs;
	}
	
	public void setArcs(Set<Arc> arcs) {
		this.arcs = arcs;
	}

	@XmlElement(name = "graphics")
	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	@XmlElement(name = "toolspecific")
	public List<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}

	public void setToolSpecifics(List<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
	}
}
