package nl.rug.ds.bpm.ptnet;

import nl.rug.ds.bpm.net.DataDrivenGraph;
import nl.rug.ds.bpm.net.element.T;
import nl.rug.ds.bpm.net.marking.DataM;
import nl.rug.ds.bpm.net.marking.M;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.NetContainer;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Variable;
import nl.rug.ds.bpm.ptnet.element.Arc;
import nl.rug.ds.bpm.ptnet.element.Node;
import nl.rug.ds.bpm.ptnet.element.Place;
import nl.rug.ds.bpm.ptnet.element.Transition;
import nl.rug.ds.bpm.ptnet.marking.DataMarking;
import nl.rug.ds.bpm.ptnet.marking.Marking;

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
	
	public DataDrivenNet(NetContainer xmlElement) {
		super(xmlElement);
		manager = new ScriptEngineManager();
	}

	@Override
	public DataMarking getInitialMarking() {
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

	@Override
	public void setInitialMarking(M marking) {
		super.setInitialMarking(marking);
		for (String v: variables.keySet())
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
	public boolean isParallelEnabled(Set<? extends T> ts, M marking) {
		boolean isParSet = true;

		//check if enough tokens exist and check if guards contradict conditions
		Iterator<? extends T> transitions = ts.iterator();
		Marking required = new Marking();

		while (isParSet && transitions.hasNext()) {
			T t = transitions.next();
			isParSet = (!(marking instanceof DataM) || evaluateGuard(t, (DataM) marking));

			for (Arc in : getIncoming((Node) t))
				required.addTokens(in.getSource().getId(), in.getWeight());
		}

		Iterator<String> placesWithRequiredTokens = required.getMarkedPlaces().iterator();
		while (isParSet && placesWithRequiredTokens.hasNext()) {
			String place = placesWithRequiredTokens.next();
			isParSet = required.getTokensAtPlace(place) <= marking.getTokensAtPlace(place);
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
		DataM marking;
		
		if(isEnabled(t, m)) {
			marking = (DataM) super.fire(t, m);

			if(m instanceof DataM && t instanceof Transition) {
				Transition transition = (Transition) t;
				if (!transition.getScript().isEmpty()) {
					ScriptEngine engine = manager.getEngineByName(transition.getScriptType());

					//Bindings only update when using createBindings, so create and clone manually
					Bindings bindings = engine.createBindings();
					bindings.putAll(((DataM)m).getBindings());

					marking.setBindings(bindings);
					engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

					try {
						engine.eval(transition.getScript());
					} catch (ScriptException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else marking = (DataM) m;
		
		return marking;
	}
	public boolean evaluateGuard(T t, DataM m) {
		boolean satisfied = t.getGuard() == null;
		
		if (!satisfied) {
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			
			try {
				satisfied = (boolean) engine.eval(t.getGuard().toString(), m.getBindings());
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		
		return satisfied;
	}
}
