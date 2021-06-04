package nl.rug.ds.bpm.petrinet.ddnet;

import nl.rug.ds.bpm.petrinet.ddnet.marking.DataMarking;
import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.DataMarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.net.VerifiableDataNet;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.NetContainer;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Variable;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.log.LogEvent;
import nl.rug.ds.bpm.util.log.Logger;

import org.graalvm.polyglot.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 18-May-18.
 */
public class DataDrivenNet extends PlaceTransitionNet implements VerifiableDataNet {
	
	public DataDrivenNet() {
		super();
	}

	public DataDrivenNet(String id) {
		super(id);
	}
	
	public DataDrivenNet(String id, String name) {
		super(id, name);
	}
	
	public DataDrivenNet(NetContainer xmlElement) throws MalformedNetException {
		super(xmlElement);
	}

	@Override
	public DataMarking getInitialMarking() {
		DataMarking m = new DataMarking();
		try {
			for (Place p: places.values())
				if (p.getTokens() > 0)
						m.addTokens(p.getId(), p.getTokens());
		} catch (IllegalMarkingException e) {
			m = new DataMarking();
			Logger.log(e.getMessage(), LogEvent.ERROR);
			e.printStackTrace();
		}

		for (Variable v : getVariables())
			m.setBinding(v.getName(), v.getValue());

		return m;
	}

	@Override
	public void setInitialMarking(MarkingI marking) throws MalformedNetException {
		super.setInitialMarking(marking);
		for (String v: new HashSet<>(variables.keySet()))
			removeVariable(v);

		if(marking instanceof DataMarkingI) {
			DataMarkingI m = (DataMarkingI) marking;

			for (String b : m.getBindings().keySet())
				if (!b.equalsIgnoreCase("nashorn.global")) {
					Object val = m.getBindings().get(b);
					if (val instanceof String)
						val = (Object) "'" + (String) val + "'";
					addVariable(b, "var", "" + val);
				}
		}
	}

	@Override
	public boolean isEnabled(TransitionI t, MarkingI m) {
		return super.isEnabled(t, m) && (!(m instanceof DataMarkingI) || evaluateGuard(t, (DataMarkingI) m));
	}

	@Override
	public boolean isParallelEnabled(Set<? extends TransitionI> ts, MarkingI m) {
		boolean isParSet = true;

		//check if enough tokens exist and check if guards contradict conditions
		Iterator<? extends TransitionI> transitions = ts.iterator();
		Marking required = (Marking) m.clone();

		while (isParSet && transitions.hasNext()) {
			TransitionI t = transitions.next();
			isParSet = (!(m instanceof DataMarkingI) || evaluateGuard(t, (DataMarkingI) m));

			try {
				for (Arc in : getIncoming((Node) t))
					required.consumeTokens(in.getSource().getId(), in.getWeight());
			} catch (IllegalMarkingException e) {
				isParSet = false;
			}
		}

		return isParSet;
	}

	//checks whether t is parallel enabled with a known parallel set
	@Override
	protected boolean isParallelEnabled(Set<? extends TransitionI> parSet, TransitionI t, MarkingI m) {
		boolean isParSet = (!(m instanceof DataMarkingI) || evaluateGuard(t, (DataMarkingI) m));
		Marking required = (Marking) m.clone();

		try {
			for (Arc in : getIncoming((Node) t))
				required.consumeTokens(in.getSource().getId(), in.getWeight());
		} catch (IllegalMarkingException e) {
			isParSet = false;
		}

		Iterator<? extends TransitionI> parIterator = parSet.iterator();
		while (isParSet && parIterator.hasNext()) {
			TransitionI p = parIterator.next();

			try {
				for (Arc in : getIncoming((Node) p))
					required.consumeTokens(in.getSource().getId(), in.getWeight());
			} catch (IllegalMarkingException e) {
				isParSet = false;
			}
		}

		return isParSet;
	}

		@Override
	public Set<? extends DataMarkingI> fireTransition(TransitionI t, MarkingI m) {
		Set<DataMarkingI> markings = new HashSet<>();
		markings.add(fire(t, m));
		return markings;
	}

	@Override
	public DataMarkingI fire(TransitionI t, MarkingI m) {
		//Marking with data, evaluates guards and script
		DataMarking marking;
		
		if(evaluateGuard(t, (DataMarkingI) m)) {
			marking = (DataMarking) super.fire(t, m);

			if(m instanceof DataMarkingI && t instanceof Transition) {
				Transition transition = (Transition) t;
				if (!transition.getScript().isEmpty()) {
					String langid = (transition.getScriptType().isEmpty() ? "js" : transition.getScriptType());
					Context polyglot = Context.create();
					Value bindings = polyglot.getBindings(langid);

					for (String key: marking.getBindings().keySet())
						bindings.putMember(key, marking.getBindings().get(key));

					polyglot.eval(langid, transition.getScript());

					for (String key: polyglot.getBindings(langid).getMemberKeys()) {
						marking.setBinding(key, polyglot.getBindings(langid).getMember(key).toString());
					}
				}
			}
		}
		else marking = (DataMarking) m;
		
		return marking;
	}
	public boolean evaluateGuard(TransitionI t, DataMarkingI m) {
		boolean satisfied = ((Transition)t).getGuard() == null;
		
		if (!satisfied) {
			Transition transition = (Transition) t;
			Context polyglot = Context.create();
			Value bindings = polyglot.getBindings("js");

			for (String key: m.getBindings().keySet())
				bindings.putMember(key, m.getBindings().get(key));

			try {
				satisfied = polyglot.eval("js", transition.getGuard().getOriginalExpression()).asBoolean();
			}
			catch (Exception e) {
				satisfied = false;
			}
		}
		
		return satisfied;
	}
}
