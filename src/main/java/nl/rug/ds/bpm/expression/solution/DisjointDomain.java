package nl.rug.ds.bpm.expression.solution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.ds.bpm.expression.AtomicExpression;
import nl.rug.ds.bpm.expression.CompositeExpression;

/*
 * Created by Hannah Burke on 22 June 2023
 * 
 * The disjoint set of continuous domains, where any assignment from any single continuous domain in the set will satisfy the given condition.
 * Note each element of this won't always be totally disjoint/mutually exclusive.
 */
public class DisjointDomain {

	private Set<ContinuousDomain> disjointSet;
	
	public DisjointDomain(Set<ContinuousDomain> disjointSet) {
		this.disjointSet = disjointSet;
	}
	
	public DisjointDomain() { // creates an infinite domain
		this.disjointSet = Set.of(new ContinuousDomain());
	}
	
	public boolean isEmpty() {
		return disjointSet.size()==0;
	}

	public String toString() {
		return disjointSet.toString();
	}

	/*
	 * From values within the current disjoint domain, returns a new disjoint domain representing the complete set of domains 
	 * for which the condition is true
	 */
	public DisjointDomain evaluate(CompositeExpression boolExpression){

		// iterates through the rows in the condition's truth table, where the overall condition evaluation is true,
		// and stores the ContinuousDomain instance(s) associated to all columns in that row being satisfied
		Set<ContinuousDomain> allSatisfyingDomains = new HashSet<>();

		List<List<AtomicExpression<?>>> all_desired_rows = boolExpression.getTable().evaluates_to(true);
		
		for (List<AtomicExpression<?>> atomics: all_desired_rows) {

			// atomics is a List of Predicate instances (ie columns of the TT)
			// which all must be true for the current TT row to be met

			// rowDomains is used to store all continuous domains such that the columns of the current TT row evaluate to true. 
			// it starts just as the original set of continuous domains in this disjoint domain, and applies each column to the current set
			Set<ContinuousDomain> rowDomains = new HashSet<>(disjointSet); 

			for (AtomicExpression<?> atom: atomics) { // iterate through the columns of the TT
				Set<ContinuousDomain> columnDomains = new HashSet<>(); // used to build the new set of continuous where the current column is true
				for (ContinuousDomain domain: rowDomains) { // iterate through the domains generated at the previous column

					Set<ContinuousDomain> columnEvaluation = domain.evaluate(atom); // from the given domain, evaluate the current atomic
					// this returns a set of domains where the atomic is true, or null if it cannot be satisfied
					if (columnEvaluation==null) { 
						continue; // if null, evaluate next domain
					}
					columnDomains.addAll(columnEvaluation); // add the resulting domains to the possible domains at this column
				}

				rowDomains = columnDomains; // update current row set to the set from the current column, before iterating over next column
			}
			allSatisfyingDomains.addAll(rowDomains); // once finished with this row, add to result
		}
		return new DisjointDomain(allSatisfyingDomains);

	}

}