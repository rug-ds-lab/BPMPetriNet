package nl.rug.ds.bpm.eventstructure.stepper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.rug.ds.bpm.expression.CompositeExpression;
import nl.rug.ds.bpm.expression.ExpressionBuilder;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.util.comparator.StringComparator;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.set.Sets;

/**
 * Created by Nick van Beest on 10-05-2018
 */
public class PTNetStepper {
	private PlaceTransitionNet ptnet;
	private Map<String, Transition> transitionmap;
	private Map<String, Place> placemap;
	private Map<String, Set<String>> transitionIdmap;
	
	// these are the guards on transitions
	private Map<Transition, Set<CompositeExpression>> transitionguardmap; 
	
	// these are the global conditions that hold for the ctl spec to be evaluated (and hence apply to the entire process)
	private Set<CompositeExpression> globalconditions; 
	
	public PTNetStepper(PlaceTransitionNet ptnet) {
		this(ptnet, new HashSet<CompositeExpression>(), new HashMap<Transition, Set<CompositeExpression>>());
	}
	
	public PTNetStepper(PlaceTransitionNet ptnet, Set<CompositeExpression> globalconditions, Map<Transition, Set<CompositeExpression>> transitionguardmap) {
		this.ptnet = ptnet;
		initializeTransitionMaps();
		initializePlaceMap();
		
		this.globalconditions = globalconditions;
		this.transitionguardmap = transitionguardmap;
	}
	
	private void initializeTransitionMaps() {
		transitionmap = new TreeMap<String, Transition>(new StringComparator());
		transitionIdmap = new TreeMap<String, Set<String>>(new StringComparator());
		
		for (Transition t: ptnet.getTransitions()) {
			transitionmap.put(t.getId(), t);
			
			if (!transitionIdmap.containsKey(t.getName()))
				transitionIdmap.put(t.getName(), new HashSet<String>());
			
			transitionIdmap.get(t.getName()).add(t.getId());
		}
	}
	
	private void initializePlaceMap() {
		placemap = new TreeMap<String, Place>(new StringComparator());
		
		for (Place p: ptnet.getPlaces()) {
			placemap.put(p.getId(), p);
		}
	}
	
	// Create a map with all enabled transitions and their corresponding bitset presets
	private Map<Transition, BitSet> getEnabledPresets(Marking marking) {
		List<Place> filled = new ArrayList<Place>();
		Set<Transition> enabled = new HashSet<Transition>();
		Map<Transition, BitSet> enabledpresets = new HashMap<Transition, BitSet>();
		
		for (String place: marking.getMarkedPlaces()) {
			filled.add(placemap.get(place));
			enabled.addAll(ptnet.getPostSet(placemap.get(place)));
		}
		
		for (Transition t: new HashSet<Transition>(enabled)) {

			if ((!filled.containsAll(ptnet.getPreSet(t))) || (contradictsConditions(t))) {  // NEW: CONTRADICTSCONDITIONS
				enabled.remove(t);
			}
			else {
				enabledpresets.put(t, getPresetBitSet(t, filled));
			}
		}
		return enabledpresets;
	}
	
	// Create a bitset that holds the positions in the list allplaces that are part of the preset of trans
	private BitSet getPresetBitSet(Transition trans, List<Place> allplaces) {
		BitSet b = new BitSet();
		
		for (Node p: ptnet.getPreSet(trans)) {
			b.set(allplaces.indexOf((Node)p));
		}
		
		return b;
	}
	
	private Boolean contradictsConditions(Transition t) {
		if ((globalconditions.size() == 0) || (!transitionguardmap.containsKey(t))) return false;

		for (CompositeExpression global: globalconditions) {
			for (CompositeExpression guard: transitionguardmap.get(t)) {
				if (guard.contradicts(global)) return true;
			}
		}
		
		return false;
	}
	
	private Boolean haveContradiction(Transition t1, Transition t2) {
		if (transitionguardmap.containsKey(t1)) {
			for (CompositeExpression e1 : transitionguardmap.get(t1)) {
				if (transitionguardmap.containsKey(t2)) {
					for (CompositeExpression e2 : transitionguardmap.get(t2)) {
						if (e1.contradicts(e2)) return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private Boolean canHaveContradiction(Transition t1, Transition t2) {
		if (transitionguardmap.containsKey(t1)) {
			for (CompositeExpression e1 : transitionguardmap.get(t1)) {
				if (transitionguardmap.containsKey(t2)) {
					for (CompositeExpression e2 : transitionguardmap.get(t2)) {
						if (e1.canContradict(e2)) return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private Boolean canHaveContradiction(Set<Transition> tset1, Set<Transition> tset2) {
		for (Transition t1: tset1) {
			for (Transition t2: tset2) {
				if (canHaveContradiction(t1, t2)) return true;
			}
		}
		
		return false;
	}
	
	public Marking getInitialMarking() {
		Marking initial = new Marking();
		
		// add all places with no incoming arcs to initial marking
		for (Place p: ptnet.getPlaces()) {
			if (ptnet.getIncoming(p).size() == 0) {
				try {
					initial.addTokens(p.getId(), 1);
				} catch (IllegalMarkingException e) {
					e.printStackTrace();
				}
			}
		}
		
		return initial;
	}
	
	public Set<Transition> getEnabledTransitions(Marking marking) {
		return getEnabledPresets(marking).keySet();
	}
	
	public Map<String, Set<String>> getTransitionIdMap() {
		return transitionIdmap;
	}
	
	public void setConditionsByExpression(Set<CompositeExpression> conditions) {
		this.globalconditions = new HashSet<CompositeExpression>(conditions);
	}
	
	public void setConditions(Set<String> conditions) {
		globalconditions = new HashSet<CompositeExpression>();
		
		for (String c: conditions) {
			globalconditions.add(ExpressionBuilder.parseExpression(c));
		}
	}
	
	public Set<CompositeExpression> getConditions() {
		return globalconditions;
	}
	
	public void setTransitionGuardsByExpression(Map<Transition, Set<CompositeExpression>> guardmap) {
		this.transitionguardmap = new HashMap<Transition, Set<CompositeExpression>>(guardmap);
	}
	
	public void setTransitionGuards(Set<String> guards) {
		transitionguardmap = new HashMap<Transition, Set<CompositeExpression>>();
		
		Transition tr;
		String t, guard;
		for (String g: guards) {
			t = g.substring(0, g.indexOf(":"));
			guard = g.substring(g.indexOf(":") + 1).trim();
			
			tr = ptnet.getTransition(t);
			if (!transitionguardmap.containsKey(tr)) transitionguardmap.put(tr, new HashSet<CompositeExpression>());
			
			transitionguardmap.get(tr).add(ExpressionBuilder.parseExpression(guard));
		}
	}
	
	public void setTransitionGuards(Map<String, Set<String>> guardmap) {
		transitionguardmap = new HashMap<Transition, Set<CompositeExpression>>();
		
		Transition tr;
		for (String t: guardmap.keySet()) {
			tr = ptnet.getTransition(t);
			if (!transitionguardmap.containsKey(tr)) transitionguardmap.put(tr, new HashSet<CompositeExpression>());
			
			for (String c: guardmap.get(t)) {
				transitionguardmap.get(tr).add(ExpressionBuilder.parseExpression(c));
			}
		}
	}
	
	public Map<Transition, Set<CompositeExpression>> getTransitionGuards() {
		return transitionguardmap;
	}
	
	public Set<Set<Transition>> parallelActivatedTransitions(Marking marking) {
		Set<Set<Transition>> ypar = new HashSet<Set<Transition>>();
		
		Map<Transition, BitSet> enabledpresets = getEnabledPresets(marking);

		if(!enabledpresets.isEmpty()) {
			// create a power set of all curently enabled transitions
			ypar = new HashSet<Set<Transition>>(Sets.powerSet(enabledpresets.keySet()));

			// remove empty set
			ypar.remove(new HashSet<String>());


			BitSet overlap;
			List<Transition> simlist;
			Boolean removed;
			for (Set<Transition> sim : new HashSet<Set<Transition>>(ypar)) {
				overlap = new BitSet();
				removed = false;

				Iterator<Transition> iterator = sim.iterator();
				while ((iterator.hasNext()) && (!removed)) {
					Transition t = iterator.next();
					// check if presets overlap for the set of transitions
					// if yes, remove (i.e. they cannot fire simultaneously)
					if (!overlap.intersects(enabledpresets.get(t))) {
						overlap.or(enabledpresets.get(t));
					}
					else {
						ypar.remove(sim);
						removed = true;
					}
				}

				// check if any of the elements contradicts with any of the others
				// if so, the entire subset sim can be removed from ypar
				if (!removed) {
					simlist = new ArrayList<Transition>(sim);
					int i = 0;
					int j;
					while ((i < simlist.size() - 1) && (!removed)) {
						j = i + 1;
						while ((j < simlist.size()) && (!removed)) {
							if (haveContradiction(simlist.get(i), simlist.get(j))) {
								removed = true;
								ypar.remove(sim);
							}
							j++;
						}
						i++;
					}
				}
			}

			Set<Set<Transition>> subsets = new HashSet<Set<Transition>>();
			Set<Transition> additional;
			// remove subsets to obtain the largest set
			for (Set<Transition> par1 : ypar) {
				for (Set<Transition> par2 : ypar) {
					if ((par1.containsAll(par2)) && (par1.size() != par2.size())) {
						// check if any of the additional elements (i.e. par1 \ par2) can contradict with par2
						// if not, subset par2 is redundant and can be removed.
						additional = new HashSet<Transition>(par1);
						additional.removeAll(par2);
						if (!canHaveContradiction(additional, par2)) subsets.add(par2);
					}
				}
			}

			ypar.removeAll(subsets);
		}
		else ypar.add(new HashSet<Transition>());

		return ypar;
	}
	
	public Marking fireTransition(Marking marking, Transition selected) {
		Marking currentfire = new Marking();
		currentfire.copyFromMarking(marking);
		
		// check if selected transition is indeed enabled
		Set<String> placeIds = new HashSet<String>();
		for (Place p: ptnet.getPreSet(selected)) {
			if (currentfire.hasTokens(p.getId())) {
				placeIds.add(p.getId());
			}
			else {
				// not enabled
				return currentfire;
			}
		}
		
		// fire
		// remove 1 token from each incoming place
		try {
			currentfire.consumeTokens(placeIds);
		} catch (IllegalMarkingException e) {
			e.printStackTrace();
		}
		
		// place 1 token in each outgoing place
		for (Place p: ptnet.getPostSet(selected)) {
			try {
				currentfire.addTokens(p.getId(), 1);
			} catch (IllegalMarkingException e) {
				e.printStackTrace();
			}
		}
		
		return currentfire;
	}
	
	public String getTransitionMap() {
		String str = "";
		for (String t: transitionmap.keySet()) {
			str += t + ": " + transitionmap.get(t).getName() + "\n";
		}
		
		return str;
	}
	
	public PlaceTransitionNet getPTNet() {
		return ptnet;
	}
}
