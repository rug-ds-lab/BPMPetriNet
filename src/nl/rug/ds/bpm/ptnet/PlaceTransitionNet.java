package nl.rug.ds.bpm.ptnet;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.NetContainer;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.Page;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.ToolSpecific;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.annotation.Name;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.Process;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Group;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Role;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Variable;
import nl.rug.ds.bpm.ptnet.element.Arc;
import nl.rug.ds.bpm.ptnet.element.Node;
import nl.rug.ds.bpm.ptnet.element.Place;
import nl.rug.ds.bpm.ptnet.element.Transition;
import nl.rug.ds.bpm.ptnet.marking.DataMarking;
import nl.rug.ds.bpm.ptnet.marking.Marking;

import javax.script.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlaceTransitionNet {
	private HashMap<String, Node> nodes;
	private HashMap<String, Place> places;
	private HashMap<String, Transition> transitions;

	private HashMap<String, Arc> arcs;
	private HashMap<String, Set<Arc>> incoming;
	private HashMap<String, Set<Arc>> outgoing;

	private HashMap<String, PlaceTransitionNet> pages;

	private HashMap<String, Group> groups;
	private HashMap<String, Variable> variables;
	private HashMap<String, Role> roles;

	private Process process;
	private NetContainer xmlElement;
	
	ScriptEngineManager manager;
	
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

		manager = new ScriptEngineManager();
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

	public PlaceTransitionNet(NetContainer xmlElement) {
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

		for (nl.rug.ds.bpm.pnml.jaxb.ptnet.node.place.Place place: xmlElement.getPlaces()) {
			Place p = new Place(place);
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.jaxb.ptnet.node.place.RefPlace place: xmlElement.getRefPlaces()) {
			Place p = new Place(place);
			places.put(place.getId(), p);
			nodes.put(place.getId(), p);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(place.getId(), in);
			outgoing.put(place.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.jaxb.ptnet.node.transition.Transition transition: xmlElement.getTransitions()) {
			Transition t = new Transition(transition);
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}
		for (nl.rug.ds.bpm.pnml.jaxb.ptnet.node.transition.RefTransition transition: xmlElement.getRefTransitions()){
			Transition t = new Transition(transition);
			transitions.put(transition.getId(), t);
			nodes.put(transition.getId(), t);
			Set<Arc> in = new HashSet<>();
			Set<Arc> out = new HashSet<>();
			incoming.put(transition.getId(), in);
			outgoing.put(transition.getId(), out);
		}

		for (nl.rug.ds.bpm.pnml.jaxb.ptnet.Arc arc: xmlElement.getArcs()) {
			Arc a = new Arc(arc);
			a.setSource(nodes.get(arc.getSource()));
			a.setTarget(nodes.get(arc.getTarget()));
			arcs.put(arc.getId(), a);
			incoming.get(arc.getTarget()).add(a);
			outgoing.get(arc.getSource()).add(a);
		}

		for (NetContainer page: xmlElement.getPages())
			pages.put(page.getId(), new PlaceTransitionNet(page));

		for (ToolSpecific toolSpecific: xmlElement.getToolSpecifics())
			if(toolSpecific.getTool().equals("nl.rug.ds.bpm.ptnet"))
				process = toolSpecific.getProcess();

		if (process == null) {
			process = new Process();
			ToolSpecific toolSpecific = new ToolSpecific();
			toolSpecific.setProcess(process);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}

		for (Group group: process.getGroups())
			groups.put(group.getId(), group);

		for (Variable variable: process.getVariables())
			variables.put(variable.getName(), variable);

		for (Role role: process.getRoles())
			roles.put(role.getId(), role);

		manager = new ScriptEngineManager();
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
	public void addTransition(Transition t) {
		transitions.put(t.getId(), t);
		nodes.put(t.getId(), t);
		Set<Arc> in = new HashSet<>();
		Set<Arc> out = new HashSet<>();
		incoming.put(t.getId(), in);
		outgoing.put(t.getId(), out);

		xmlElement.getTransitions().add((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.transition.Transition) t.getXmlElement());
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

	public Collection<Transition> getTransitions() {
		return transitions.values();
	}

	//Place methods
	public void addPlace(Place p) {
		places.put(p.getId(), p);
		nodes.put(p.getId(), p);

		Set<Arc> in = new HashSet<>();
		Set<Arc> out = new HashSet<>();
		incoming.put(p.getId(), in);
		outgoing.put(p.getId(), out);

		xmlElement.getPlaces().add((nl.rug.ds.bpm.pnml.jaxb.ptnet.node.place.Place) p.getXmlElement());
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

	public Collection<Place> getPlaces() {
		return places.values();
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
	public PlaceTransitionNet addPage(String id) {
		Page page = new Page(id);
		xmlElement.getPages().add(page);

		PlaceTransitionNet net = new PlaceTransitionNet(page);
		pages.put(id, net);

		return net;
	}

	public PlaceTransitionNet addPage(String id, String name) {
		PlaceTransitionNet net = addPage(id);
		net.setName(name);
		return net;
	}

	public void removePage(PlaceTransitionNet p) {
		pages.remove(p.getId());
		xmlElement.getPages().remove(p.getXmlElement());
	}

	public void removePage(String id) {
		if (pages.containsKey(id))
			removePage(pages.get(id));
	}

	public PlaceTransitionNet getPage(String id) {
		return pages.get(id);
	}

	public Collection<PlaceTransitionNet> getPages() {
		return pages.values();
	}

	//Variable methods
	public void addVariable(Variable v) {
		variables.put(v.getName(), v);
		process.getVariables().add(v);
	}

	public Variable addVariable(String name, String type, String value) {
		Variable v = new Variable(name, type, value);
		addVariable(v);
		return v;
	}

	public Variable addVariable(String name, String type) {
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
	public void addGroup(Group g) {
		groups.put(g.getId(), g);
		process.getGroups().add(g);
	}

	public Group addGroup(String id, String name) {
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
	public void addRole(Role r) {
		roles.put(r.getId(), r);
		process.getRoles().add(r);
	}

	public Role addRole(String id, String name) {
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

	public Collection<Node> getPreSet(Node n) {
		Set<Node> pre = new HashSet<>();
		for (Arc a: incoming.get(n.getId()))
			pre.add(a.getSource());
		return pre;
	}

	public Collection<Node> getPostSet(Node n) {
		Set<Node> post = new HashSet<>();
		for (Arc a: outgoing.get(n.getId()))
			post.add(a.getTarget());
		return post;
	}
	
	public Marking getInitialMarking() {
		Marking m = new Marking();
		for (Place p: places.values())
			if (p.getTokens() > 0)
				m.addTokens(p.getId(), p.getTokens());
		return m;
	}
	
	public DataMarking getInitialDataMarking() {
		DataMarking m = new DataMarking();
		for (Place p: places.values())
			if (p.getTokens() > 0)
				m.addTokens(p.getId(), p.getTokens());
		
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try {
			for (Variable v : getVariables()) {
				String var = "var " + v.getName();
				if (!v.getValue().isEmpty())
					var = var + " = " + v.getValue() + ";";
				engine.eval(var);
			}
			m.setBindings(engine.getBindings(ScriptContext.ENGINE_SCOPE));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		return m;
	}

	public void setInitialMarking(Marking marking) {
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

	public void setInitialDataMarking(DataMarking marking) {
		setInitialMarking((Marking) marking);
		for (String v: variables.keySet())
			removeVariable(v);

		for (String b: marking.getTrackedBindings()) {
			Object val = marking.getBindings().get(b);
			if (val instanceof String)
				val = (Object) "'" + (String)val + "'";
			addVariable(b, "var", "" + val);
		}
	}

	public boolean isEnabled(Transition t, Marking m) {
		//Marking without data, disregards guards
		boolean enabled = true;
		Iterator<Arc> iterator = incoming.get(t.getId()).iterator();

		while (enabled && iterator.hasNext()) {
			Arc in = iterator.next();
			enabled = in.getWeight() <= m.getTokensAtPlace(in.getSource().getId());
		}

		return enabled;
	}
	
	public boolean isEnabled(Transition t, DataMarking m) {
		//Marking with data, evaluates guards
		return isEnabled(t, (Marking) m) && evaluateGuard(t, m);
	}
	
	public Collection<Transition> getEnabled(Marking m) {
		//Marking without data, disregards guards
		return transitions.values().stream().filter(t -> isEnabled(t, m)).collect(Collectors.toSet());
	}
	
	public Collection<Transition> getEnabled(DataMarking m) {
		//Marking with data, evaluates guards
		return transitions.values().stream().filter(t -> isEnabled(t, m)).collect(Collectors.toSet());
	}

	public Marking fire(Transition t, Marking m) {
		//Marking without data, disregards guards and script
		Marking marking = m.clone();

		if(isEnabled(t, m)) {
			for (Arc in: incoming.get(t.getId()))
				marking.consumeTokens(in.getSource().getId(), in.getWeight());
			for (Arc out: outgoing.get(t.getId()))
				marking.addTokens(out.getTarget().getId(), out.getWeight());
		}

		return marking;
	}
	
	public DataMarking fire(Transition t, DataMarking m) {
		//Marking with data, evaluates guards and script
		DataMarking marking;
		
		if(isEnabled(t, m)) {
			marking = (DataMarking) fire(t, (Marking) m);
			
			if (!t.getScript().isEmpty()) {
				ScriptEngine engine = manager.getEngineByName(t.getScriptType());
				
				//Bindings only update when using createBindings, so create and clone manually
				Bindings bindings = engine.createBindings();
				bindings.putAll(m.getBindings());
				
				marking.setBindings(bindings);
				engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
				
				try {
					engine.eval(t.getScript());
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		}
		else marking = m;
		
		return marking;
	}
	
	public boolean evaluateGuard(Transition t, DataMarking m) {
		boolean satisfied = t.getGuard() == null || t.getGuard().isEmpty();
		
		if (!satisfied) {
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			
			try {
				satisfied = (boolean) engine.eval(t.getGuard(), m.getBindings());
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		
		return satisfied;
	}

	public boolean isNSafe(int n) {
		boolean nSafe = true;

		Iterator<Place> placeIterator = places.values().iterator();
		while (nSafe && placeIterator.hasNext())
			nSafe = placeIterator.next().getTokens() <= n;

		Iterator<Arc> arcIterator = arcs.values().iterator();
		while (nSafe && arcIterator.hasNext())
			nSafe = arcIterator.next().getWeight() <= n;

		return nSafe;
	}
}
