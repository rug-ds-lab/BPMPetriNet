package nl.rug.ds.bpm.pnml.jaxb.core;

import nl.rug.ds.bpm.pnml.jaxb.core.annotation.Name;
import nl.rug.ds.bpm.pnml.jaxb.core.node.place.Place;
import nl.rug.ds.bpm.pnml.jaxb.core.node.place.RefPlace;
import nl.rug.ds.bpm.pnml.jaxb.core.node.transition.RefTransition;
import nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition;

import java.util.Set;

public interface NetContainer {
	public String getId();
	public void setId(String id);

	public Name getName();
	public void setName(Name name);

	public Graphics getGraphics();
	public void setGraphics(Graphics graphics);

	public Set<ToolSpecific> getToolSpecifics();
	public void setToolSpecifics(Set<ToolSpecific> toolSpecifics);

	public Set<Place> getPlaces();
	public void setPlaces(Set<Place> places);

	public Set<RefPlace> getRefPlaces();
	public void setRefPlaces(Set<RefPlace> refPlaces);

	public Set<Transition> getTransitions();
	public void setTransitions(Set<Transition> transitions);

	public Set<RefTransition> getRefTransitions();
	public void setRefTransitions(Set<RefTransition> refTransitions);

	public Set<Arc> getArcs();
	public void setArcs(Set<Arc> arcs);

	public Set<Page> getPages();
	public void setPages(Set<Page> pages);
}
