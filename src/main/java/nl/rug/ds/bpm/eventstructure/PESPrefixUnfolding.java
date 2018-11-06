package nl.rug.ds.bpm.eventstructure;

import nl.rug.ds.bpm.expression.CompositeExpression;
import nl.rug.ds.bpm.petrinet.interfaces.element.PlaceI;
import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.ConditionalMarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.net.UnfoldableNet;
import nl.rug.ds.bpm.util.comparator.PairComparator;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.pair.Pair;

import java.util.*;

/**
 * Created by Nick van Beest on 10 May 2018
 *
 */
public class PESPrefixUnfolding {
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
	private BitSet correspondings;
	private Map<Integer, Integer> ccmap; // from cutoff to corresponding
	private Map<Integer, Integer> tmpcc;
	
	private Set<Pair<MarkingI, TransitionI>> visited;
		
	private int initial, sink;
	
	public PESPrefixUnfolding(UnfoldableNet ptnet, String silentPrefix) throws MalformedNetException {
		this(ptnet, new HashSet<CompositeExpression>(), silentPrefix);
	}
	
	public PESPrefixUnfolding(UnfoldableNet ptnet, Set<CompositeExpression> globalconditions, String silentPrefix) throws MalformedNetException {
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
		correspondings = new BitSet();
		ccmap = new HashMap<Integer, Integer>();
		tmpcc = new HashMap<Integer, Integer>();
		
		visited = new TreeSet<Pair<MarkingI, TransitionI>>(new PairComparator<MarkingI, TransitionI>());

		Collection<? extends PlaceI> sinks = ptnet.getSinks();
		
		// sinkcount must be 1, otherwise it is not a proper workflow net
		if (sinks.size() == 1) {
			PlaceI sinkplace = sinks.iterator().next();
			if (ptnet.getPreSet(sinkplace).size() > 1) {
				ptnet.addTransition("artificial_end");
				ptnet.addPlace("artificial_sink");
				ptnet.addArc(sinkplace.getId(), "artificial_end");
				ptnet.addArc("artificial_end", "artificial_sink");
			}

			for (TransitionI t: ptnet.getTransitions()) {
				if (t.getName().startsWith(silentPrefix)) t.setTau(true);
			}
			
			buildPES(ptnet, globalconditions, silentPrefix);
		}
		else throw new MalformedNetException("Not a workflow net: Could not find a unique sink place.");
	}
	
	private void buildPES(UnfoldableNet ptnet, Set<CompositeExpression> globalconditions, String silentPrefix) throws MalformedNetException {
		MarkingI marking = ptnet.getInitialMarking();
		
		if (marking.getMarkedPlaces().isEmpty()) {
			throw new MalformedNetException("Initial marking empty, no tokens on any place.");
		}
		else {
			if(marking instanceof ConditionalMarkingI)
				for (CompositeExpression e: globalconditions)
					((ConditionalMarkingI)marking).addCondition(e.toString(), e);
					
			progressPES(ptnet, marking, null);
	
			fillDirectConflictRelations();
			fillConflictRelations();
		}
	}
	
	private void progressPES(UnfoldableNet ptnet, MarkingI marking, TransitionI last) {
		Collection<? extends TransitionI> enabled = ptnet.getEnabledTransitions(marking);
		Set<? extends Set<? extends TransitionI>> parenabled = ptnet.getParallelEnabledTransitions(marking);
		
		// fill in concurrency
		BitSet partialconc;
		for (Set<? extends TransitionI> conc: parenabled) {
			partialconc = new BitSet();
			for (TransitionI c: conc) {
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
		for (TransitionI selected: enabled) {
			
			addLabel(selected);
			
			if ((!visited.contains(new Pair<MarkingI, TransitionI>(marking, selected))) || (!isCausal(last, selected))) {
					
				visited.add(new Pair<MarkingI, TransitionI>(marking, selected));

				if (last != null) {
					if (!isConcurrent(last, selected)) {
						addSuccessor(fulllabels.indexOf(last.toString()), fulllabels.indexOf(selected.toString()));
					}
				}
				else {
					initial = fulllabels.indexOf(selected.toString());
				}

				for (MarkingI next: ptnet.fireTransition(selected, marking))
					progressPES(ptnet, next, selected);
				// Returns a set because future nets with guards on arcs may
				// produce multiple possible future markings (e.g., CPN).
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
	
	private void addLabel(TransitionI tr) {
		String label = tr.toString();
		
		if (!fulllabels.contains(label)) {
			fulllabels.add(label);
			labels.add(tr.getName());
			
			if (tr.isTau()) invisibles.set(labels.size() - 1);
		}
	}
	
	private Boolean isCausal(TransitionI t1, TransitionI t2) {
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
	
	private Boolean isConcurrent(TransitionI t1, TransitionI t2) {
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
		if (dpred.get(target).cardinality() > 1)  {
			BitSet tmp = new BitSet();
//			tmp.or(concurrency.get(source));
			tmp.or(dpred.get(target));
			tmp.andNot(concurrency.get(source));
			if ((tmp.cardinality() > 1) && (!correspondings.get(source))) {
				cutoffs.set(source);
				
				if (!tmpcc.containsKey(target)) {
					tmp.andNot(cutoffs);
					tmpcc.put(target, tmp.nextSetBit(0));
				}
				correspondings.set(tmpcc.get(target));
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
