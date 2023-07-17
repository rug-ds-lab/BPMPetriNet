package nl.rug.ds.bpm.expression.solution;

import java.util.HashSet;
import java.util.Set;

import nl.rug.ds.bpm.expression.AtomicPredicate;

/*
 * Created by Hannah Burke on 22 June 2023
 * 
 * A continuous set of values generated from the possible values that satisfy a given atomic expression. currently supported for bools and doubles
 */
public interface Domain{

	public boolean contains(Object value); // returns whether the domain contains the value
	public Set<Domain> evaluate(AtomicPredicate<?> input); // from the current domain, returns the set of sub-domains where the input predicate is satisfied 
	
	public static Domain infinite(Object input) { // generates a domain representing all possible values for the input type
		if (input instanceof Boolean) return Bool.infinite();
		if (input instanceof Number) return Range.infinite();
		throw new RuntimeException("Cannot make infinite domain of type "+input.getClass());
	}

	/*
	 * represents the possible values for a boolean variable
	 * can be
	 * {true}
	 * {false}
	 * {true, false}
	 */
	public static class Bool implements Domain{

		public final Boolean value; // either true, false, or null (meaning either)

		public Bool(Boolean value) {
			this.value = value;
		}
		
		public String toString() {	
			if (value==null) return "null";
			return value.toString();
		}
		
		public static Bool infinite() {
			return new Bool(null);
		}

		@Override
		public boolean contains(Object input) {
			if (input instanceof Boolean) {
				if (value==null) return true;
				return value.equals(input);
			}
			return false;
		}

		public Set<Domain> evaluate(AtomicPredicate<?> input) {
			switch (input.getExpressionType()){
			case EQ:
				if (this.contains(input.getValue())) {
					if (value==null) return Set.of(new Bool((Boolean)input.getValue()));
					return Set.of(this);
				}
				return null;
			case NEQ:
				if (this.contains(!(Boolean)input.getValue())) {
					if (value==null) return Set.of(new Bool(!(Boolean)input.getValue()));
					return Set.of(this);
				}
				return null;
			default:
				throw new RuntimeException("Comparator not valid");
			} 
		}
	}


	/*
	 * Inclusive range for Double variables
	 */
	public static class Range implements Domain{

		public final Double lower;
		public final Double upper;

		public Range(double lower, double upper) {
			if (lower>upper) {
				throw new InvalidRangeException("Cannot create range: "+lower+", "+upper);
			}
			this.lower = lower;
			this.upper = upper;
		}
		
		public String toString() {
			StringBuilder result = new StringBuilder("");
			if (lower==-Double.MAX_VALUE) {
				result.append("(-Infinity,");
			}
			else {
				result.append("["+lower.toString()+",");
			}
			if (upper==Double.MAX_VALUE) {
				result.append("Infinity)");		}
			else {
				result.append(upper.toString()+"]");
			}
			return result.toString();
		}
		
		public static Range infinite() {
			return new Range(-Double.MAX_VALUE, Double.MAX_VALUE);
		}

		@Override
		public boolean contains(Object input) {
			if (input instanceof Double) {
				Double Double = (Double) input;
				return Double>= lower && Double<=upper;
			}
			return false;
		}

		@Override
		public Set<Domain> evaluate(AtomicPredicate<?> input) {
			switch (input.getExpressionType()){
			case EQ:
				if (this.contains(input.getValue())) { // if val is in input, return val, otherwise null
					return Set.of(new Range((Double)input.getValue(), (Double)input.getValue()));
				}
				return null;
			case NEQ:
				Set<Domain> result = new HashSet<>();
				try {
					result.add(new Range(this.lower, Math.min((Double)input.getValue()-Math.ulp((Double)input.getValue()), this.upper)));
				}
				catch (InvalidRangeException e) {}

				try {
					result.add(new Range(Math.max((Double)input.getValue()+Math.ulp((Double)input.getValue()), this.lower), this.upper));
				}
				catch (InvalidRangeException e) {}
				if (result.size()==0) {
					return null;
				}
				return result;
			case GEQ:
				try {
					return Set.of(new Range(Math.max((Double)input.getValue(), this.lower), this.upper));
				}
				catch (InvalidRangeException e) {
					return null;
				}

			case LEQ:
				try {
					return Set.of(new Range(this.lower, Math.min((Double)input.getValue(), this.upper)));
				}
				catch (InvalidRangeException e) {
					return null;
				}

			case GT:
				try {
					return Set.of(new Range(Math.max((Double)input.getValue()+Math.ulp((Double)input.getValue()), this.lower), this.upper));
				}
				catch (InvalidRangeException e) {
					return null;
				}
			case LT:
				try {
					return Set.of(new Range(this.lower, Math.min((Double)input.getValue()-Math.ulp((Double)input.getValue()), this.upper)));
				}
				catch (InvalidRangeException e) {
					return null;
				}		
			default:
				throw new RuntimeException("Comparator not valid");
			}
		}
	}

	@SuppressWarnings("serial")
	public static class InvalidRangeException extends RuntimeException{
		public InvalidRangeException(String e) {
			super(e);
		}
	}
}

