package nl.rug.ds.bpm.petrinet.interfaces.marking;

import java.util.Collection;
import java.util.HashMap;

import nl.rug.ds.bpm.expression.CompositeExpression;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface ConditionalM extends M {
	void setConditions(HashMap<String, CompositeExpression> conditions);
	Collection<CompositeExpression> getConditions();

	void addCondition(String condition);
	void addCondition(String condition, CompositeExpression expression);
	void removeCondition(String condition);
}
