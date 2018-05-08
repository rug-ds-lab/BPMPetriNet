package nl.rug.ds.bpm.ptnet.marking;

import nl.rug.ds.bpm.comparator.StringComparator;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.process.Variable;

import javax.script.Bindings;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class DataMarking extends Marking {
	private Bindings bindings;
	private Set<String> tracked;

	public DataMarking() {
		super();
		tracked = new TreeSet<>(new StringComparator());
	}
	
	public Bindings getBindings() {
		return bindings;
	}
	
	public void setBindings(Bindings bindings) {
		this.bindings = bindings;
	}
	
	public Collection<String> getTrackedBindings() {
		return tracked;
	}

	public Collection<String> trackedToString() {
		Set<String> bind = new HashSet<>();
		
		for(String b: tracked)
			bind.add(b + "=" + bindings.get(b).toString());
		
		return bind;
	}
	
	public void setTrackedBindings(Set<String> tracked) {
		this.tracked.addAll(tracked);
	}
	
	public void addBindingTracking(Variable var) {
		tracked.add(var.getName());
	}
	
	public void addBindingTracking(String name) {
		tracked.add(name);
	}
	
	public void removeBindingTracking(Variable var) {
		tracked.remove(var.getName());
	}
	
	public void removeBindingTracking(String name) {
		tracked.remove(name);
	}
	
	public boolean containsBinding(String variable) {
		return bindings.containsKey(variable);
	}
	
	@Override
	public DataMarking clone() {
		DataMarking dataMarking = new DataMarking();
		dataMarking.copyFromMarking(this);
		
		//Bindings only update within ScriptEngine when using createBindings, not with new SimpleBindings.
		//Remember to create and clone Bindings manually after calling clone()
		
		//dataMarking.setBindings(new SimpleBindings(bindings));  //this fails to update
		
		dataMarking.setTrackedBindings(tracked);
		
		return dataMarking;
	}
}
