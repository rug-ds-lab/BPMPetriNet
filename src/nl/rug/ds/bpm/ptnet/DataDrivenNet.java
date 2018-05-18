package nl.rug.ds.bpm.ptnet;

import nl.rug.ds.bpm.net.DataDrivenGraph;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.NetContainer;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Variable;
import nl.rug.ds.bpm.ptnet.element.Place;
import nl.rug.ds.bpm.ptnet.element.Transition;
import nl.rug.ds.bpm.ptnet.marking.DataMarking;
import nl.rug.ds.bpm.ptnet.marking.Marking;

import javax.script.*;
import java.util.Collection;
import java.util.stream.Collectors;

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
	
	public boolean isEnabled(Transition t, DataMarking m) {
		//Marking with data, evaluates guards
		return isEnabled(t, (Marking) m) && evaluateGuard(t, m);
	}
	
	public Collection<Transition> getEnabledTransitions(DataMarking m) {
		//Marking with data, evaluates guards
		return transitions.values().stream().filter(t -> isEnabled(t, m)).collect(Collectors.toSet());
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
	
	
}
