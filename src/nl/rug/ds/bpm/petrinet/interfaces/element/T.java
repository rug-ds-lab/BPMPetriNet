package nl.rug.ds.bpm.petrinet.interfaces.element;

import nl.rug.ds.bpm.expression.CompositeExpression;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface T {
	String getId();
	String getName();
	CompositeExpression getGuard();
	boolean isTau();
}
