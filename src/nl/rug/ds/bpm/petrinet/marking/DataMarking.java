package nl.rug.ds.bpm.petrinet.marking;

import nl.rug.ds.bpm.comparator.StringComparator;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Variable;

import java.util.*;

public class DataMarking extends Marking {
	private Map<String, Variable> variables;
	private Set<String> tracked;

	public DataMarking() {
		super();
		tracked = new TreeSet<>(new StringComparator());
		variables = new TreeMap<>(new StringComparator());
	}

	public void addVariable(Variable var) {
		Variable v = new Variable(var.getName(), var.getType(), var.getValue());
		variables.put(var.getName(), v);
	}

	public Variable getVariable(String name) {
		return variables.get(name);
	}

	public Collection<Variable> getVariables() {
		return variables.values();
	}

	public void addVariables(Collection<Variable> vars) {
		for (Variable variable: vars)
			addVariable(variable);
	}

	public boolean setVariableValue(String variable, String value) {
		boolean exists = variables.containsKey(variable);
		if (exists)
			variables.get(variable).setValue(value);
		return exists;
	}
	
	public Set<String> getTrackedVariables() {
		return tracked;
	}
	
	public void setTrackedVariables(Set<String> tracked) {
		this.tracked.addAll(tracked);
	}
	
	public void addVariableTracking(Variable var) {
		addVariable(var);
		tracked.add(var.getName());
	}
	
	public void addVariableTracking(String name) {
		if (variables.containsKey(name))
			tracked.add(name);
	}
	
	public void removeVariableTracking(Variable var) {
		tracked.remove(var.getName());
	}
	public void removeVariableTracking(String name) {
		tracked.remove(name);
	}
	
	public boolean variableExists(String variable) {
		return variables.containsKey(variable);
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		
		Iterator<String> t = tracked.iterator();
		while (t.hasNext()) {
			Variable v = variables.get(t.next());
			s = s + "|" + v.getName() + "=" + v.getValue();
		}
		
		return s;
	}
	
	@Override
	public DataMarking clone() {
		DataMarking dataMarking = new DataMarking();
		dataMarking.copyFromMarking(this);
		dataMarking.addVariables(variables.values());
		dataMarking.setTrackedVariables(tracked);
		
		return dataMarking;
	}
}
