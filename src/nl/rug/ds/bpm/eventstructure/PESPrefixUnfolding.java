package nl.rug.ds.bpm.eventstructure;

import nl.rug.ds.bpm.comparator.MarkingComparator;
import nl.rug.ds.bpm.eventstructure.stepper.PTNetStepper;
import nl.rug.ds.bpm.expression.Expression;
import nl.rug.ds.bpm.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.ptnet.element.Transition;
import nl.rug.ds.bpm.ptnet.marking.Marking;

import java.util.*;

/**
 * Created by Nick van Beest on 10 May 2018
 *
 */
public class PESPrefixUnfolding {
	private List<String> labels;
	private List<String> fulllabels;
	
	private Map<Integer, BitSet> dcausality;
	private Map<Integer, BitSet> tcausality;
	private Map<Integer, BitSet> pred;
	private Map<Integer, BitSet> tpred;
	private Map<Integer, BitSet> conflict;
	private Map<Integer, BitSet> concurrency;
	
	private BitSet tmpvisited;
	
	private BitSet cutoffs;
	
	private Set<Marking> visited;
	
	private int initial, sink;
	
	public PESPrefixUnfolding(PlaceTransitionNet ptnet) {
		this(ptnet, new HashSet<Expression<?>>(), new HashMap<Transition, Set<Expression<?>>>());
	}
	
	public PESPrefixUnfolding(PlaceTransitionNet ptnet, Set<Expression<?>> globalconditions, Map<Transition, Set<Expression<?>>> transitionguardmap) {
		labels = new ArrayList<String>();
		fulllabels = new ArrayList<String>();
		
		dcausality = new HashMap<Integer, BitSet>();
		tcausality = new HashMap<Integer, BitSet>();
		pred = new HashMap<Integer, BitSet>();
		tpred = new HashMap<Integer, BitSet>();
		conflict = new HashMap<Integer, BitSet>();
		concurrency = new HashMap<Integer, BitSet>();
		
		cutoffs = new BitSet();
		
		tmpvisited = new BitSet();
		
		visited = new TreeSet<Marking>(new MarkingComparator());
		
		buildPES(ptnet, globalconditions, transitionguardmap);
	}
	
	private void buildPES(PlaceTransitionNet ptnet, Set<Expression<?>> globalconditions, Map<Transition, Set<Expression<?>>> transitionguardmap) {
		PTNetStepper stepper = new PTNetStepper(ptnet, globalconditions, transitionguardmap);
		
		Marking marking = stepper.getInitialMarking();
		
		progressPES(stepper, marking, null);
		
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
						addCausality(fulllabels.indexOf(last.toString()), fulllabels.indexOf(selected.toString()));
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
	
	private void fillTransitiveCausality(int e) {
		if (e != initial) {
			for (int p = pred.get(e).nextSetBit(0); p >= 0; p = pred.get(e).nextSetBit(p + 1)) {
				if (!tcausality.containsKey(p)) tcausality.put(p, new BitSet()); 
				
				if (!tcausality.get(p).get(e)) {
					if (tcausality.containsKey(e)) tcausality.get(p).or(tcausality.get(e));
					tcausality.get(p).set(e);
					fillTransitiveCausality(p);
				}
			}
		}
	}
	
	private void fillTransitivePred(int e) {
		if (e != sink) {
			for (int p = dcausality.get(e).nextSetBit(0); p >= 0; p = dcausality.get(e).nextSetBit(p + 1)) {
				if (!tpred.containsKey(p)) tpred.put(p, new BitSet()); 
				
				if (!tpred.get(p).get(e)) {
					if (tpred.containsKey(e)) tpred.get(p).or(tpred.get(e));
					tpred.get(p).set(e);
					fillTransitivePred(p);
				}
			}
		}
	}
	
	private void fillConflictRelations() {
		BitSet conf;
		
//		for (int c = cutoffs.nextSetBit(0); c >= 0; c = cutoffs.nextSetBit(c + 1)) {
//			fillTransitiveCausality(c);
//		}
		fillTransitiveCausality(sink);
		
		fillTransitivePred(initial);
		
		for (int e = 0; e < fulllabels.size(); e++) {
			conf = new BitSet();
			if (tcausality.containsKey(e)) conf.or(tcausality.get(e));
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
		}
	}
	
	private Boolean isCausal(Transition t1, Transition t2) {
		int index1, index2;
		
		if ((t1 == null) || (t2 == null)) return false;
		
		index1 = fulllabels.indexOf(t1.toString());
		index2 = fulllabels.indexOf(t2.toString());
		
		if (dcausality.containsKey(index1)) {
			if (dcausality.get(index1).get(index2)) {
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
	
	private void addCausality(int source, int target) {
		if (!dcausality.containsKey(source)) dcausality.put(source, new BitSet());
		dcausality.get(source).set(target);
		
		if (!pred.containsKey(target)) pred.put(target, new BitSet());
		pred.get(target).set(source);
		if (pred.get(target).cardinality() > 1) {
			BitSet tmp = new BitSet();
			tmp.or(concurrency.get(source));
			tmp.xor(pred.get(target));
			if (tmp.cardinality() > 1) {
				cutoffs.set(source);
			}
		}
		
//		if (!tpred.containsKey(target)) tpred.put(target, new BitSet());
//		if (!tpred.get(target).get(source)) {
//			if (tpred.containsKey(source)) tpred.get(target).or(tpred.get(source));
//			tpred.get(target).set(source);
//		}
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
	
	public BitSet getCausality(int event) {
		if (dcausality.containsKey(event)) {
			return dcausality.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getTransitiveCausality(int event) {
		if (tcausality.containsKey(event)) {
			return tcausality.get(event);
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
	
	public BitSet getConflict(int event) {
		if (conflict.containsKey(event)) {
			return conflict.get(event);
		}
		else {
			return new BitSet();
		}
	}
	
	public BitSet getCutoffs() {
		return cutoffs;
	}
}
