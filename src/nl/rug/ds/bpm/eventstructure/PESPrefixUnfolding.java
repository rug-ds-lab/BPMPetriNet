package nl.rug.ds.bpm.eventstructure;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.rug.ds.bpm.eventstructure.stepper.PTNetStepper;
import nl.rug.ds.bpm.expression.Expression;
import nl.rug.ds.bpm.petrinet.interfaces.unfolding.Unfolding;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.util.comparator.MarkingComparator;

/**
 * Created by Nick van Beest on 10 May 2018
 *
 */
public class PESPrefixUnfolding implements Unfolding {
	private List<String> labels;
	private List<String> fulllabels;
	private BitSet invisibles;
	
	private Map<Integer, BitSet> dsucc;
	private Map<Integer, BitSet> tsucc;
	private Map<Integer, BitSet> dpred;
	private Map<Integer, BitSet> tpred;
	private Map<Integer, BitSet> conflict;
	private Map<Integer, BitSet> concurrency;
	private Map<Integer, BitSet> dconflict;
		
	private BitSet cutoffs;
	private Map<Integer, Integer> ccmap; // from cutoff to corresponding
	private Map<Integer, Integer> tmpcc;
	
	private Set<Marking> visited;
		
	private int initial, sink;
	
	public PESPrefixUnfolding(PlaceTransitionNet ptnet) {
		this(ptnet, new HashSet<Expression<?>>(), new HashMap<Transition, Set<Expression<?>>>());
	}
	
	public PESPrefixUnfolding(PlaceTransitionNet ptnet, Set<Expression<?>> globalconditions, Map<Transition, Set<Expression<?>>> transitionguardmap) {
		labels = new ArrayList<String>();
		fulllabels = new ArrayList<String>();
		invisibles = new BitSet();
		
		dsucc = new HashMap<Integer, BitSet>();
		tsucc = new HashMap<Integer, BitSet>();
		dpred = new HashMap<Integer, BitSet>();
		tpred = new HashMap<Integer, BitSet>();
		conflict = new HashMap<Integer, BitSet>();
		concurrency = new HashMap<Integer, BitSet>();
		dconflict = new HashMap<Integer, BitSet>();
		
		cutoffs = new BitSet();
		ccmap = new HashMap<Integer, Integer>();
		tmpcc = new HashMap<Integer, Integer>();
		
		visited = new TreeSet<Marking>(new MarkingComparator());
				
		buildPES(ptnet, globalconditions, transitionguardmap);
	}
	
	private void buildPES(PlaceTransitionNet ptnet, Set<Expression<?>> globalconditions, Map<Transition, Set<Expression<?>>> transitionguardmap) {
		PTNetStepper stepper = new PTNetStepper(ptnet, globalconditions, transitionguardmap);
		
		Marking marking = stepper.getInitialMarking();
		
		progressPES(stepper, marking, null);
		
		fillDirectConflictRelations();
		fillConflictRelations();
	}
	
	private void progressPES(PTNetStepper stepper, Marking marking, Transition last) {
		Set<Transition> enabled = stepper.getEnabledTransitions(marking);
		Set<Set<Transition>> parenabled = stepper.parallelActivatedTransitions(marking);
		
		// fill in concurrency
		BitSet partialconc;
		for (Set<Transition> conc: parenabled) {
			partialconc = new BitSet();
			for (Transition c: conc) {
				addLabel(c);
				partialconc.set(fulllabels.indexOf(c.getId() + "_" + c.getName()));
			}
			for (int b = partialconc.nextSetBit(0); b >= 0; b = partialconc.nextSetBit(b + 1)) {
				if (!concurrency.containsKey(b)) concurrency.put(b, new BitSet());
				concurrency.get(b).or(partialconc);
				concurrency.get(b).clear(b);
			}	
		}
		
		// fill in causality
		Marking next;
		for (Transition selected: enabled) {
			
			addLabel(selected);
			
			if ((!visited.contains(marking)) || (!isCausal(last, selected))) {
				visited.add(marking);
				
				next = stepper.fireTransition(marking, selected);
					
				if (last != null) {
					if (!isConcurrent(last, selected)) {
						addSuccessor(fulllabels.indexOf(last.toString()), fulllabels.indexOf(selected.toString()));
					}
				}
				else {
					initial = fulllabels.indexOf(selected.toString());
				}
				
				progressPES(stepper, next, selected);
			}
		}
		
		if (enabled.size() == 0) {
			sink = fulllabels.indexOf(last.toString());
		}
	}
	
	private BitSet fillTransitiveSuccessors(int e, BitSet past) {
		BitSet succ = new BitSet();
		
		if (e != sink) {
			BitSet npast = new BitSet();
			npast.or(past);
			npast.set(e);
			for (int p = dsucc.get(e).nextSetBit(0); p >= 0; p = dsucc.get(e).nextSetBit(p + 1)) {
				if (!past.get(p)) {
					succ.set(p);
					succ.or(fillTransitiveSuccessors(p, npast));
				}
			}
			if (!tsucc.containsKey(e)) tsucc.put(e, succ);
		}
		
		return succ;
	}
	
	private BitSet fillTransitivePredecessors(int e, BitSet past) {
		BitSet pred = new BitSet();
		
		if (e != initial) {
			BitSet npast = new BitSet();
			npast.or(past);
			if (cutoffs.get(e)) npast.set(e);
			for (int p = dpred.get(e).nextSetBit(0); p >= 0; p = dpred.get(e).nextSetBit(p + 1)) {
				if (!past.get(p)) {
					pred.set(p);
					pred.or(fillTransitivePredecessors(p, npast));
				}
			}
			if (!tpred.containsKey(e)) tpred.put(e, pred);
		}
		
		return pred;
	}
	
	private void fillDirectConflictRelations() {
		BitSet conf;
		
		for (int e: dsucc.keySet()) {
			if (dsucc.get(e).cardinality() > 1) {
				for (int dc = dsucc.get(e).nextSetBit(0); dc >= 0; dc = dsucc.get(e).nextSetBit(dc + 1)) {
					conf = new BitSet();
					conf.or(dsucc.get(e));
				
					// check if there are any events in the postset of e that are concurrent and remove
					if (concurrency.containsKey(dc)) conf.andNot(concurrency.get(dc));
						
					if (!dconflict.containsKey(dc)) dconflict.put(dc, new BitSet());
						
					dconflict.get(dc).or(conf);
					dconflict.get(dc).clear(dc);
				}
			}
		}
	}
	
	private void fillConflictRelations() {
		BitSet conf;
		
		fillTransitiveSuccessors(initial, new BitSet());
		fillTransitivePredecessors(sink, new BitSet());
		
		for (int e = 0; e < fulllabels.size(); e++) {
			conf = new BitSet();
			if (tsucc.containsKey(e)) conf.or(tsucc.get(e));
			if (concurrency.containsKey(e)) conf.or(concurrency.get(e));
			if (tpred.containsKey(e)) conf.or(tpred.get(e));
			
			conf.flip(0, fulllabels.size());
			conf.clear(e);
			if (conf.cardinality() > 0) conflict.put(e, conf);
		}
	}
	
	private void addLabel(Transition tr) {
		String label = tr.toString();
		
		if (!fulllabels.contains(label)) {
			fulllabels.add(label);
			labels.add(tr.getName());
			
			if (tr.isTau()) invisibles.set(labels.size() - 1);
		}
	}
	
	private Boolean isCausal(Transition t1, Transition t2) {
		int index1, index2;
		
		if ((t1 == null) || (t2 == null)) return false;
		
		index1 = fulllabels.indexOf(t1.toString());
		index2 = fulllabels.indexOf(t2.toString());
		
		if (dsucc.containsKey(index1)) {
			if (dsucc.get(index1).get(index2)) {
				return true;
			}
		}
		
		return false;
	}
	
	private Boolean isConcurrent(Transition t1, Transition t2) {
		int index1, index2;
		
		if ((t1 == null) || (t2 == null)) return false;
		
		index1 = fulllabels.indexOf(t1.toString());
		index2 = fulllabels.indexOf(t2.toString());

		if (concurrency.containsKey(index1)) {
			if (concurrency.get(index1).get(index2)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void addSuccessor(int source, int target) {
		if (!dsucc.containsKey(source)) dsucc.put(source, new BitSet());
		dsucc.get(source).set(target);
		
		if (!dpred.containsKey(target)) dpred.put(target, new BitSet());
		dpred.get(target).set(source);
		if (dpred.get(target).cardinality() > 1) {
			BitSet tmp = new BitSet();
			tmp.or(concurrency.get(source));
			tmp.xor(dpred.get(target));
			if (tmp.cardinality() > 1) {
				cutoffs.set(source);
				
				if (!tmpcc.containsKey(target)) {
					tmp.andNot(cutoffs);
					tmpcc.put(target, tmp.nextSetBit(0));
				}
				ccmap.put(source, tmpcc.get(target));
			}
		}
	}
	
	public int getInitial() {
		return initial;
	}
	
	public int getSink() {
		return sink;
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public String getLabel(int event) {
		return labels.get(event);
	}
	
	public BitSet getInvisibleEvents() {
		return invisibles;
	}
	
	public BitSet getDirectSuccessors(int event) {
		if (dsucc.containsKey(event)) {
			return dsucc.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getDirectPredecessors(int event) {
		if (dpred.containsKey(event)) {
			return dpred.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getTransitiveSuccessors(int event) {
		if (tsucc.containsKey(event)) {
			return tsucc.get(event);
		}
		else {
			return new BitSet();
		}
	}

	public BitSet getTransitivePredecessors(int event) {
		if (tpred.containsKey(event)) {
			return tpred.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getConcurrency(int event) {
		if (concurrency.containsKey(event)) {
			return concurrency.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getConflicts(int event) {
		if (conflict.containsKey(event)) {
			return conflict.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getDirectConflicts(int event) {
		if (dconflict.containsKey(event)) {
			return dconflict.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getCutoffEvents() {
		return cutoffs;
	}
	
	public int getCorrespondingEvent(int cutoff) {
		if (ccmap.containsKey(cutoff)) {
			return ccmap.get(cutoff);
		}
		else {
			return -1;
		}
	}
	
}
