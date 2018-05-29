package nl.rug.ds.bpm.petrinet.interfaces.marking;

import nl.rug.ds.bpm.expression.Expression;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface ConditionalM extends M {
	void setConditions(HashMap<String, Expression<?>> conditions);
	Collection<Expression<?>> getConditions();

	void addCondition(String condition);
	void removeCondition(String condition);
}
