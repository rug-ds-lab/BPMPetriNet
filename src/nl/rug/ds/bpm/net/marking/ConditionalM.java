package nl.rug.ds.bpm.net.marking;

import nl.rug.ds.bpm.expression.Expression;

import java.util.Collection;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface ConditionalM extends M {
	Collection<Expression<?>> getConditions();
}
