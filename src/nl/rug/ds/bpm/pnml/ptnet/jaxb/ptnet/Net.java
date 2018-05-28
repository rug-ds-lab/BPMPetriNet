package nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Name;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.RefPlace;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition.RefTransition;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition.Transition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "net")
public class Net implements NetContainer {
	private String id;
	private Name name;
	private String type = "http://www.pnml.org/version-2009/grammar/petrinet";
	private Graphics graphics;
	private Set<ToolSpecific> toolSpecifics;
	private Set<Place> places;
	private Set<RefPlace> refPlaces;
	private Set<Transition> transitions;
	private Set<RefTransition> refTransitions;
	private Set<Arc> arcs;
	private Set<Page> pages;
	
	public Net() {
		toolSpecifics = new HashSet<>();
		places = new HashSet<>();
		refPlaces = new HashSet<>();
		transitions = new HashSet<>();
		refTransitions = new HashSet<>();
		arcs = new HashSet<>();
		pages = new HashSet<>();
	}
	
	public Net(String id) {
		this();
		this.id = id;
	}
	
	@XmlAttribute(name = "id", required = true)
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
	
	@XmlAttribute(name = "type", required = true)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "graphics")
	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	@XmlElement(name = "toolspecific")
	public Set<ToolSpecific> getToolSpecifics() {
		return toolSpecifics;
	}

	public void setToolSpecifics(Set<ToolSpecific> toolSpecifics) {
		this.toolSpecifics = toolSpecifics;
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
