package nl.rug.ds.bpm.expression;

/*
 * Created by Hannah Burke on 17 July 2023
 * 
 * Interface for an atomic expression expression, which is either:
 * - a predicate (x>0, a==true, etc)
 * - a tautology (true, false)
 */
public interface AtomicExpression<T extends Comparable<T>> {
	
	/*
	 * returns the complement of the expression
	 */
	public AtomicExpression<T> negate();

}
