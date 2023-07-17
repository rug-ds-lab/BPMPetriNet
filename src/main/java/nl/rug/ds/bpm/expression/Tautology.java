package nl.rug.ds.bpm.expression;

/*
 * Created by Hannah Burke on 17 July 2023
 * 
 * Class for an expression that is simply "true" or "false"
 */
public class Tautology implements AtomicExpression<Boolean> {
	
	public final Boolean isTrue;
	private Tautology(boolean value) {
		this.isTrue = value;
	}
	
	public static final Tautology trueTautology = new Tautology(true);
	public static final Tautology falseTautology = new Tautology(false);

	@Override
	public AtomicExpression<Boolean> negate() {
		if (isTrue) return falseTautology;
		else return trueTautology;
	}

	@Override
	public int compareTo(AtomicExpression<Boolean> o) {
		if (this==o) return 0;
		// essentially they're not equal, but we need to do a bit of work to ensure transitivity?
		Integer hashcode = this.hashCode();
		Integer compareTo = hashcode.compareTo(o.hashCode());
		if (compareTo==0) throw new RuntimeException("Problematic equality in hashcodes of expressions: "+this+" and "+o);
		return compareTo;
	}
	
	@Override
	public String toString() {
		return isTrue.toString();
	}

}
