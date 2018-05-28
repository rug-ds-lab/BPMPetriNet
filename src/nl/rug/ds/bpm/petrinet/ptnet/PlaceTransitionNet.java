package nl.rug.ds.bpm.petrinet.ptnet;

import nl.rug.ds.bpm.expression.Expression;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.NetContainer;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Page;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.ToolSpecific;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.annotation.Name;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.Process;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Group;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Role;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Variable;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.interfaces.element.T;
import nl.rug.ds.bpm.util.interfaces.graph.TransitionGraph;
import nl.rug.ds.bpm.util.interfaces.marking.ConditionalM;
import nl.rug.ds.bpm.util.interfaces.marking.M;
import nl.rug.ds.bpm.util.interfaces.unfolding.unfolding;
import nl.rug.ds.bpm.util.set.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceTransitionNet implements TransitionGraph, unfolding {
	protected HashMap<String, Node> nodes;
	protected HashMap<String, Place> places;
	protected HashMap<String, Transition> transitions;

	protected HashMap<String, Arc> arcs;
	protected HashMap<String, Set<Arc>> incoming;
	protected HashMap<String, Set<Arc>> outgoing;

	protected HashMap<String, NetContainer> pages;

	protected HashMap<String, Group> groups;
	protected HashMap<String, Variable> variables;
	protected HashMap<String, Role> roles;

	protected Process process;
	protected NetContainer xmlElement;
	
	public PlaceTransitionNet() {
		nodes = new HashMap<>();
		places = new HashMap<>();
		transitions = new HashMap<>();

		arcs = new HashMap<>();
		incoming = new HashMap<>();
		outgoing = new HashMap<>();

		pages = new HashMap<>();

		groups = new HashMap<>();
		variables = new HashMap<>();
		roles = new HashMap<>();

		process = new Process();
		ToolSpecific toolSpecific = new ToolSpecific();
		toolSpecific.setProcess(process);
		xmlElement = new Net();
		xmlElement.getToolSpecifics().add(toolSpecific);
	}

	public PlaceTransitionNet(String id) {
		this();
		xmlElement.setId(id);
	}

	public PlaceTransitionNet(String id, String name) {
		this();
		xmlElement.setId(id);
		xmlElement.setName(new Name(name));
	}

	public PlaceTransitionNet(NetContainer xmlElement) throws MalformedNetException {
		nodes = new HashMap<>();
		places = new HashMap<>();
		transitions = new HashMap<>();

		arcs = new HashMap<>();
		incoming = new HashMap<>();
		outgoing = new HashMap<>();

		pages = new HashMap<>();

		groups = new HashMap<>();
		variables = new HashMap<>();
		roles = new HashMap<>();

		this.xmlElement = xmlElement;

		for (nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place place: xmlElement.getPlaces()) {
			Place p = new Place(place);
			if (nodes.containsKey(place.getId()))
				throw new MalformedNetException("Duplicate node i.d.: " + place.getId() + ".");
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.RefPlace place: xmlElement.getRefPlaces()) {
			Place p = new Place(place);
			if (nodes.containsKey(place.getId()))
				throw new MalformedNetException("Duplicate node i.d.: " + place.getId() + ".");
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition.Transition transition: xmlElement.getTransitions()) {
			Transition t = new Transition(transition);
			if (nodes.containsKey(transition.getId()))
				throw new MalformedNetException("Duplicate node i.d.: " + transition.getId() + ".");
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition.RefTransition transition: xmlElement.getRefTransitions()){
			Transition t = new Transition(transition);
			if (nodes.containsKey(transition.getId()))
				throw new MalformedNetException("Duplicate node i.d.: " + transition.getId() + ".");
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Arc arc: xmlElement.getArcs()) {
			Arc a = new Arc(arc);
			if (arcs.containsKey(arc.getId()))
				throw new MalformedNetException("Duplicate arc i.d.: " + arc.getId() + ".");
			a.setSource(nodes.get(arc.getSource()));
			a.setTarget(nodes.get(arc.getTarget()));
			arcs.put(arc.getId(), a);
			incoming.get(arc.getTarget()).add(a);
			outgoing.get(arc.getSource()).add(a);
		}

		for (NetContainer page: xmlElement.getPages()) {
			if (pages.containsKey(page.getId()))
				throw new MalformedNetException("Duplicate page i.d.: " + page.getId() + ".");
			pages.put(page.getId(), page);
		}

		for (ToolSpecific toolSpecific: xmlElement.getToolSpecifics())
			if(toolSpecific.getTool().equals("nl.rug.ds.bpm.ptnet"))
				process = toolSpecific.getProcess();

		if (process == null) {
			process = new Process();
			ToolSpecific toolSpecific = new ToolSpecific();
			toolSpecific.setProcess(process);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}

		for (Group group: process.getGroups()) {
			if (groups.containsKey(group.getId()))
				throw new MalformedNetException("Duplicate group i.d.: " + group.getId() + ".");
			groups.put(group.getId(), group);
		}

		for (Variable variable: process.getVariables()) {
			if (variables.containsKey(variable.getName()))
				throw new MalformedNetException("Duplicate variable name.: " + variable.getName() + ".");
			variables.put(variable.getName(), variable);
		}

		for (Role role: process.getRoles()) {
			if (roles.containsKey(role.getId()))
				throw new MalformedNetException("Duplicate role i.d.: " + role.getId() + ".");
			roles.put(role.getId(), role);
		}
	}

	//Net methods
	public String getId() {
		return xmlElement.getId();
	}

	public void setId(String id) {
		xmlElement.setId(id);
	}

	public String getName() {
		String n = "";
		Name name = xmlElement.getName();

		if(name != null)
			n = name.getText().getText();

		return n;
	}

	public void setName(String name) {
		xmlElement.setName(new Name(name));
	}

	public NetContainer getXmlElement() {
		return xmlElement;
	}

	//Transition methods
	public void addTransition(Transition t) throws MalformedNetException {
		if (nodes.containsKey(t.getId()))
			throw new MalformedNetException("Duplicate node i.d.: " + t.getId() + ".");
		else {
			transitions.put(t.getId(), t);
			nodes.put(t.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(t.getId(), in);
			outgoing.put(t.getId(), out);
			
			xmlElement.getTransitions().add((nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.transition.Transition) t.getXmlElement());
		}
	}

	public Transition addTransition(String id) throws MalformedNetException {
		Transition t = new Transition(id);
		addTransition(t);
		return t;
	}

	public Transition addTransition(String id, String name) throws MalformedNetException {
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
	
	public Collection<Transition> getTransitions() {
		return transitions.values();
	}

	//Place methods
	public void addPlace(Place p) throws MalformedNetException {
		if (nodes.containsKey(p.getId()))
			throw new MalformedNetException("Duplicate node i.d.: " + p.getId() + ".");
		else {
			places.put(p.getId(), p);
			nodes.put(p.getId(), p);
			
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(p.getId(), in);
			outgoing.put(p.getId(), out);
			
			xmlElement.getPlaces().add((nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.node.place.Place) p.getXmlElement());
		}
	}

	public Place addPlace(String id) throws MalformedNetException {
		Place p = new Place(id);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, int tokens) throws MalformedNetException {
		Place p = new Place(id);
		p.setTokens(tokens);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, String name) throws MalformedNetException {
		Place p = new Place(id, name);
		addPlace(p);
		return p;
	}

	public Place addPlace(String id, String name, int tokens) throws MalformedNetException {
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

	public Collection<Place> getPlaces() {
		return places.values();
	}

	//Arc methods
	public void addArc(Arc a) throws MalformedNetException {
		if (arcs.containsKey(a.getId()))
			throw new MalformedNetException("Duplicate arc i.d.: " + a.getId() + ".");
		else {
			arcs.put(a.getId(), a);
			incoming.get(a.getTarget().getId()).add(a);
			outgoing.get(a.getSource().getId()).add(a);
			
			xmlElement.getArcs().add(a.getXmlElement());
		}
	}

	public Arc addArc(Node source, Node target) throws MalformedNetException {
		Arc a = new Arc(source.getId() + "-" + target.getId(), source, target);
		addArc(a);
		return a;
	}

	public Arc addArc(Node source, Node target, int weight) throws MalformedNetException {
		Arc a = new Arc(source.getId() + "-" + target.getId(), source, target);
		a.setWeight(weight);
		addArc(a);
		return a;
	}

	public Arc addArc(String sourceId, String targetId) throws MalformedNetException {
		if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId))
			return null;
		return addArc(nodes.get(sourceId), nodes.get(targetId));
	}

	public Arc addArc(String sourceId, String targetId, int weight) throws MalformedNetException {
		if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId))
			return null;
		Arc a = addArc(nodes.get(sourceId), nodes.get(targetId));
		a.setWeight(weight);
		return a;
	}

	public void removeArc(Arc a) {
		arcs.remove(a.getId());
		incoming.get(a.getTarget().getId()).remove(a);
		outgoing.get(a.getSource().getId()).remove(a);
		xmlElement.getArcs().remove(a.getXmlElement());
	}

	public void removeArc(String id) {
		if (arcs.containsKey(id))
			removeArc(arcs.get(id));
	}

	public Arc getArc(String id) {
		return arcs.get(id);
	}

	public Collection<Arc> getArcs() {
		return arcs.values();
	}

	private void removeArcs(Node n) {
		Set<Arc> connections = new HashSet<>(incoming.get(n.getId()));
		connections.addAll(outgoing.get(n.getId()));

		for (Arc a: connections)
			removeArc(a);
	}

	//Page methods
	public NetContainer addPage(String id) throws MalformedNetException {
		Page page = new Page(id);
		if (pages.containsKey(id))
			throw new MalformedNetException("Duplicate page i.d.: " + id + ".");
		else {
			xmlElement.getPages().add(page);
			pages.put(id, page);
		}
		return page;
	}

	public NetContainer addPage(String id, String name) throws MalformedNetException {
		NetContainer net = addPage(id);
		net.setName(new Name(name));
		return net;
	}

	public void removePage(NetContainer p) {
		pages.remove(p.getId());
		xmlElement.getPages().remove(p);
	}

	public void removePage(String id) {
		if (pages.containsKey(id))
			removePage(pages.get(id));
	}

	public NetContainer getPage(String id) {
		return pages.get(id);
	}

	public Collection<NetContainer> getPages() {
		return pages.values();
	}

	//Variable methods
	public void addVariable(Variable v) throws MalformedNetException {
		if (variables.containsKey(v.getName()))
			throw new MalformedNetException("Duplicate variable name: " + v.getName() + ".");
		else {
			variables.put(v.getName(), v);
			process.getVariables().add(v);
		}
	}

	public Variable addVariable(String name, String type, String value) throws MalformedNetException {
		Variable v = new Variable(name, type, value);
		addVariable(v);
		return v;
	}

	public Variable addVariable(String name, String type) throws MalformedNetException {
		Variable v = new Variable(name, type);
		addVariable(v);
		return v;
	}

	public void removeVariable(Variable v) {
		process.getVariables().remove(v);
		variables.remove(v.getName());
	}

	public void removeVariable(String name) {
		if (variables.containsKey(name))
			removeVariable(variables.get(name));
	}

	public Variable getVariable(String name) {
		return variables.get(name);
	}

	public Collection<Variable> getVariables() {
		return process.getVariables();
	}

	//Group methods
	public void addGroup(Group g) throws MalformedNetException {
		if (groups.containsKey(g.getId()))
			throw new MalformedNetException("Duplicate group i.d.: " + g.getId() + ".");
		else {
			groups.put(g.getId(), g);
			process.getGroups().add(g);
		}
	}

	public Group addGroup(String id, String name) throws MalformedNetException {
		Group g = new Group(id, name);
		addGroup(g);
		return g;
	}

	public void removeGroup(Group g) {
		process.getGroups().remove(g);
		groups.remove(g.getId());
	}

	public void removeGroup(String id) {
		if (groups.containsKey(id))
			removeGroup(groups.get(id));
	}

	public Group getGroup(String id) {
		return groups.get(id);
	}

	public Collection<Group> getGroups() {
		return process.getGroups();
	}

	//Role methods
	public void addRole(Role r) throws MalformedNetException {
		if (roles.containsKey(r.getId()))
			throw new MalformedNetException("Duplicate role i.d.: " + r.getId() + ".");
		else {
			roles.put(r.getId(), r);
			process.getRoles().add(r);
		}
	}

	public Role addRole(String id, String name) throws MalformedNetException {
		Role r = new Role(id, name);
		addRole(r);
		return r;
	}

	public void removeRole(Role r) {
		process.getRoles().remove(r);
		roles.remove(r.getId());
	}

	public void removeRole(String id) {
		if (roles.containsKey(id))
			removeRole(roles.get(id));
	}

	public Role getRole(String id) {
		return roles.get(id);
	}

	public Collection<Role> getRoles() {
		return process.getRoles();
	}

	//Utility methods
	public boolean isSink(Place p) {
		return places.containsKey(p.getId()) && outgoing.get(p.getId()).isEmpty();
	}

	public boolean isSink(String id) {
		return places.containsKey(id) && outgoing.get(id).isEmpty();
	}

	public Collection<Place> getSinks() {
		return places.values().stream().filter(p -> isSink(p)).collect(Collectors.toSet());
	}

	public boolean isSource(Place p) {
		return places.containsKey(p.getId()) && incoming.get(p.getId()).isEmpty();
	}

	public boolean isSource(String id) {
		return places.containsKey(id) && incoming.get(id).isEmpty();
	}

	public Collection<Place> getSources() {
		return places.values().stream().filter(p -> isSource(p)).collect(Collectors.toSet());
	}

	public Collection<Place> getPreSet(Transition n) {
		Set<Place> pre = new HashSet<>();
		for (Arc a: incoming.get(n.getId())) {
			if (a.getSource() instanceof Place) pre.add((Place)a.getSource());
		}
		return pre;
	}
	
	public Collection<Transition> getPreSet(Place n) {
		Set<Transition> pre = new HashSet<>();
		for (Arc a: incoming.get(n.getId())) {
			if (a.getSource() instanceof Transition) pre.add((Transition)a.getSource());
		}
		return pre;
	}
	
	public Collection<Arc> getIncoming(Node n) {
		Set<Arc> iarcs = new HashSet<Arc>();
		iarcs.addAll(incoming.get(n.getId()));
		return iarcs;
	}
	
	public Collection<Place> getPostSet(Transition n) {
		Set<Place> post = new HashSet<>();
		for (Arc a: outgoing.get(n.getId())) {
			if (a.getTarget() instanceof Place) post.add((Place)a.getTarget());
		}
		return post;
	}
	
	public Collection<Transition> getPostSet(Place n) {
		Set<Transition> post = new HashSet<>();
		for (Arc a: outgoing.get(n.getId())) {
			if (a.getTarget() instanceof Transition) post.add((Transition)a.getTarget());
		}
		return post;
	}
	
	public Collection<Arc> getOutgoing(Node n) {
		Set<Arc> oarcs = new HashSet<Arc>();
		oarcs.addAll(outgoing.get(n.getId()));
		return oarcs;
	}
	
	public Marking getInitialMarking() {
		Marking m = new Marking();
		try {
			for (Place p: places.values())
				if (p.getTokens() > 0)
						m.addTokens(p.getId(), p.getTokens());
		} catch (IllegalMarkingException e) {
			m = new Marking();
			e.printStackTrace();
		}
			
		return m;
	}

	public void setInitialMarking(M marking) throws MalformedNetException {
		Marking old = getInitialMarking();
		for (String p: old.getMarkedPlaces()) {
			Place place = getPlace(p);
			if(place != null)
				place.setTokens(0);
		}

		for (String p: marking.getMarkedPlaces()) {
			Place place = getPlace(p);
			if(place != null)
				place.setTokens(marking.getTokensAtPlace(p));
		}
	}

	public boolean isEnabled(T t, M m) {
		boolean enabled = true;
		Iterator<Arc> iterator = incoming.get(t.getId()).iterator();

		while (enabled && iterator.hasNext()) {
			Arc in = iterator.next();
			enabled = in.getWeight() <= m.getTokensAtPlace(in.getSource().getId());
		}
		
		return (m instanceof ConditionalM ? enabled && isEnabledUnderCondition(t, (ConditionalM) m) : enabled);
	}

	private boolean isEnabledUnderCondition(T t, ConditionalM m) {
		boolean enabled = true;
		if(t.getGuard() != null) {
			Iterator<Expression<?>> guardIterator = m.getConditions().iterator();

			while (enabled && guardIterator.hasNext())
				enabled = !t.getGuard().contradicts(guardIterator.next());
		}

		return enabled;
	}

	@Override
	public boolean isParallelEnabled(Set<? extends T> ts, M m) {
		//check if guards contradict
		boolean isParSet = !areContradictory(ts);

		//check if enough tokens exist and check if guards contradict conditions
		Iterator<? extends T> transitions = ts.iterator();
		Marking required = new Marking();

		while (isParSet && transitions.hasNext()) {
			T t = transitions.next();

			if(m instanceof ConditionalM)
				isParSet = isEnabledUnderCondition(t, (ConditionalM) m);
			
			try {
				for (Arc in : getIncoming((Node) t))
						required.addTokens(in.getSource().getId(), in.getWeight());
			} catch (IllegalMarkingException e) {
				isParSet = false;
				e.printStackTrace();
			}
		}

		Iterator<String> placesWithRequiredTokens = required.getMarkedPlaces().iterator();
		while (isParSet && placesWithRequiredTokens.hasNext()) {
			String place = placesWithRequiredTokens.next();
			isParSet = required.getTokensAtPlace(place) <= m.getTokensAtPlace(place);
		}

		return isParSet;
	}
	
	public Collection<? extends T> getEnabledTransitions(M m) {
		return transitions.values().stream().filter(t -> isEnabled(t, m)).collect(Collectors.toSet());
	}

	public M fire(T t, M m) {
		Marking marking = (Marking) m.clone();

		if (isEnabled(t, m)){
			try {
				for (Arc in : incoming.get(t.getId()))
					marking.consumeTokens(in.getSource().getId(), in.getWeight());
				for (Arc out : outgoing.get(t.getId()))
					marking.addTokens(out.getTarget().getId(), out.getWeight());
			} catch (IllegalMarkingException e) {
				marking = (Marking) m.clone();
				e.printStackTrace();
			}
		}
		return marking;
	}
	
	public Set<? extends M> fireTransition(T t, M m) {
		Set<M> markings = new HashSet<>();
		markings.add(fire(t, m));
		return markings;
	}
	
	public Set<? extends Set<? extends T>> getParallelEnabledTransitions(M marking) {
		Set<Transition> enabled = (Set<Transition>) getEnabledTransitions(marking);
		Set<Set<Transition>> ypar = new HashSet<>(Sets.powerSet(enabled));
		
		Set<Set<Transition>> pruned = new HashSet<>();
		for (Set<Transition> parSet: ypar) {
			boolean isParSet = isParallelEnabled(parSet, marking);
			//check if other transitions exists that don't contradict and are enabled in par
			if (isParSet) {
				Set<Transition> otherEnabled = new HashSet<>(enabled);
				otherEnabled.removeAll(ypar);

				Iterator<Transition> otherIterator = otherEnabled.iterator();
				while (isParSet && otherIterator.hasNext()) {
					Set<Transition> parSetPlus = new HashSet<>(parSet);
					parSetPlus.add(otherIterator.next());
					isParSet = canHaveContradiction(parSetPlus, parSet) || !isParallelEnabled(parSetPlus, marking);
				}
			}
			if(!isParSet)
				pruned.add(parSet);
		}
		
		ypar.removeAll(pruned);
		return ypar;
	}

	protected boolean canHaveContradiction(Set<Transition> parSetPlus, Set<Transition> parSet) {
		//TODO awaiting Expression
		return true;
	}
	
	private boolean areContradictory(T t1, T t2) {
		return t1.getGuard() != null && t2.getGuard() != null && t1.getGuard().contradicts(t2.getGuard());
	}

	private boolean areContradictory(Set<? extends  T> tset) {
		for (T t1: tset)
			for (T t2: tset)
				if(t1 != t2)
					if (areContradictory(t1, t2))
						return true;
		
		return false;
	}
}
