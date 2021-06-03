package nl.rug.ds.bpm.petrinet.ddnet.marking;

import nl.rug.ds.bpm.petrinet.interfaces.marking.DataMarkingI;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;

import java.util.HashMap;
import java.util.Map;

public class DataMarking extends Marking implements DataMarkingI {
	private Map<String, String> bindings;

	public DataMarking() {
		super();
		bindings = new HashMap<>();
	}
	
	public Map<String, String> getBindings() {
		return bindings;
	}
	
	public void setBinding(String key, String value) {
		this.bindings.put(key, value);
	}

	public void setBindings(Map<String, String> bindings) {
		this.bindings.putAll(bindings);
	}

	public boolean containsBinding(String variable) {
		return bindings.containsKey(variable);
	}
	
	@Override
	public DataMarking clone() {
		DataMarking dataMarking = new DataMarking();
		dataMarking.copyFromMarking(this);
		dataMarking.setBindings(bindings);
		
		return dataMarking;
	}
}
