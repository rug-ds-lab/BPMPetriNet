package nl.rug.ds.bpm.petrinet.ptnet.marking;

import nl.rug.ds.bpm.util.interfaces.marking.DataM;

import javax.script.Bindings;

public class DataMarking extends Marking implements DataM {
	private Bindings bindings;

	public DataMarking() {
		super();
	}
	
	public Bindings getBindings() {
		return bindings;
	}
	
	public void setBindings(Bindings bindings) {
		this.bindings = bindings;
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
		
		return dataMarking;
	}
}
