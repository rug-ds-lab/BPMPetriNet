package nl.rug.ds.bpm.util.interfaces.element;

import nl.rug.ds.bpm.expression.Expression;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface T {
	String getId();
	String getName();
	Expression<?> getGuard();
	boolean isTau();
}
