package nl.rug.ds.bpm.petrinet.ddnet;

import nl.rug.ds.bpm.petrinet.ddnet.marking.DataMarking;
import nl.rug.ds.bpm.petrinet.interfaces.element.T;
import nl.rug.ds.bpm.petrinet.interfaces.graph.DataDrivenGraph;
import nl.rug.ds.bpm.petrinet.interfaces.marking.DataM;
import nl.rug.ds.bpm.petrinet.interfaces.marking.M;
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

import javax.script.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 18-May-18.
 */
public class DataDrivenNet extends PlaceTransitionNet implements DataDrivenGraph {
	private ScriptEngineManager manager;
	
	public DataDrivenNet() {
		super();
		manager = new ScriptEngineManager();
	}
	
	
	public DataDrivenNet(String id) {
		super(id);
		manager = new ScriptEngineManager();
	}
	
	public DataDrivenNet(String id, String name) {
		super(id, name);
		manager = new ScriptEngineManager();
	}
	
	public DataDrivenNet(NetContainer xmlElement) throws MalformedNetException {
		super(xmlElement);
		manager = new ScriptEngineManager();
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

	@Override
	public void setInitialMarking(M marking) throws MalformedNetException {
		super.setInitialMarking(marking);
		for (String v: new HashSet<>(variables.keySet()))
			removeVariable(v);

		if(marking instanceof DataM) {
			DataM m = (DataM) marking;

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
	public boolean isEnabled(T t, M m) {
		return super.isEnabled(t, m) && (!(m instanceof DataM) || evaluateGuard(t, (DataM) m));
	}

	@Override
	public boolean isParallelEnabled(Set<? extends T> ts, M m) {
		boolean isParSet = true;

		//check if enough tokens exist and check if guards contradict conditions
		Iterator<? extends T> transitions = ts.iterator();
		Marking required = (Marking) m.clone();

		while (isParSet && transitions.hasNext()) {
			T t = transitions.next();
			isParSet = (!(m instanceof DataM) || evaluateGuard(t, (DataM) m));

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
	protected boolean isParallelEnabled(Set<? extends T> parSet, T t, M m) {
		boolean isParSet = (!(m instanceof DataM) || evaluateGuard(t, (DataM) m));
		Marking required = (Marking) m.clone();

		try {
			for (Arc in : getIncoming((Node) t))
				required.consumeTokens(in.getSource().getId(), in.getWeight());
		} catch (IllegalMarkingException e) {
			isParSet = false;
		}

		Iterator<? extends T> parIterator = parSet.iterator();
		while (isParSet && parIterator.hasNext()) {
			T p = parIterator.next();

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
	public Set<? extends DataM> fireTransition(T t, M m) {
		Set<DataM> markings = new HashSet<>();
		markings.add(fire(t, m));
		return markings;
	}

	@Override
	public DataM fire(T t, M m) {
		//Marking with data, evaluates guards and script
		DataMarking marking;
		
		if(evaluateGuard(t, (DataM) m)) {
			marking = (DataMarking) super.fire(t, m);
			
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			
			//Bindings only update when using createBindings, so create and clone manually
			Bindings bindings = engine.createBindings();
			bindings.putAll(((DataM)m).getBindings());
			marking.setBindings(bindings);

			if(m instanceof DataM && t instanceof Transition) {
				Transition transition = (Transition) t;
				if (!transition.getScript().isEmpty()) {
					engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

					try {
						engine.eval(transition.getScript());
					} catch (ScriptException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else marking = (DataMarking) m;
		
		return marking;
	}
	public boolean evaluateGuard(T t, DataM m) {
		boolean satisfied = t.getGuard() == null;
		
		if (!satisfied) {
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			engine.setBindings(m.getBindings(), ScriptContext.ENGINE_SCOPE);
			
			try {
				satisfied = (boolean) engine.eval(t.getGuard().toString());
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		
		return satisfied;
	}
}
