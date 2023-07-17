package nl.rug.ds.bpm.expression.solution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.rug.ds.bpm.expression.AtomicExpression;
import nl.rug.ds.bpm.expression.AtomicPredicate;
import nl.rug.ds.bpm.expression.Tautology;

/*
 * Created by Hannah Burke on 22 June 2023
 * 
 * a map of variable names and domains, 
 * where any assignment of values for each variable will satisfy the given set of predicates
 * if an entry doesn't exist for a given variable, it is assumed that variable can take on any value within its type's codomain
 */
@SuppressWarnings("serial")
public class ContinuousDomain extends HashMap<String, Domain>{

	/*
	 * returns the disjoint set of domains where the current map satisfies the given predicate
	 * ie. takes the current domain for the predicate's variable, and finds its possible sub-domains where the predicate is true 
	 * (which is usually just one sub-domain, but can be two distinct ranges in the case of != for doubles), 
	 * then returns a map for each of these sub-domains, where that sub-domain is put that at the key for the predicate's variable
	 */
	public Set<ContinuousDomain> evaluate(AtomicExpression<?> expression){

		if (expression instanceof Tautology) {
			Tautology tautology = (Tautology) expression;
			if (tautology.isTrue) return Set.of(this);
			else return new HashSet<>();
		}
		else {
			AtomicPredicate<?> predicate = (AtomicPredicate<?>) expression;

			Domain currentDomain = this.getOrDefault(predicate.getVariableName(), Domain.infinite(predicate.getValue()));
			Set<Domain> true_vals = currentDomain.evaluate(predicate); // returns all possible Domain instances from the currentDomain allowing for a true predicate, or null if not possible

			if (true_vals==null) return null; 
			else { //clone the current map and update the key for the predicate's variable to be each of the elements of true_vals
				Set<ContinuousDomain> true_states = new HashSet<>();
				for (Domain true_val: true_vals) {
					ContinuousDomain clone = new ContinuousDomain();
					for (Entry<String, Domain> entry: this.entrySet()) {
						clone.put(entry.getKey(), entry.getValue());
					}
					clone.put(predicate.getVariableName(), true_val);
					true_states.add(clone);
				}
				return true_states; // then return all of these cloned maps
			}
		}
	}
}