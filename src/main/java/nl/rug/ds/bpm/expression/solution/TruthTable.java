package nl.rug.ds.bpm.expression.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.rug.ds.bpm.expression.AtomicExpression;
import nl.rug.ds.bpm.expression.LogicalType;
import nl.rug.ds.bpm.expression.Tautology;

/*
 * Created by Hannah Burke on 22 June 2023
 * 
 * Class for storing the possible evaluation combinations of a composite condition's individual atomics 
 * such that a given evaluation for the overall condition is achieved
 * 
 * Constructed for a composite condition by combining the truth tables of each condition in the composition
 */
public class TruthTable {
	
	private List<AtomicExpression<?>> atomics; // the row headers
    private List<AtomicExpression<?>> inverse_atomics = new ArrayList<>(); // stores the complement of each header, for quick calculations
	private List<List<Boolean>> rows; // rows[i,j] is the evaluation of the atomic at atomics[j] corresponding to the i-th row in the truth table
    private List<Boolean> evals; // evals[i] is the overall evaluation of the composite condition at the i-th row in the truth table
    
    private TruthTable(List<AtomicExpression<?>> atomics) {
    	this.atomics = atomics;
    	for (AtomicExpression<?> atom: atomics) {
    		inverse_atomics.add(atom.negate());
    	}
    	rows = new ArrayList<>();
    	evals = new ArrayList<>();
    }

    /*
     * Constructor for empty truth table
     */
    public TruthTable() {
    	this(Arrays.asList(Tautology.trueTautology));
		rows.add(Arrays.asList(true));
		evals.add(true);
    }

    /**
     * Constructor for a condition with a single atomic. Add one row for true and one for false
     */
    public TruthTable(AtomicExpression<?> atomic) {
    	this(Arrays.asList(atomic));
		// add one row for true and one row for false
		rows.add(Arrays.asList(true));
		evals.add(true);
		rows.add(Arrays.asList(false));
		evals.add(false);
    }
    
    /*
     * returns number of rows in truth table
     */
    public Integer height() {
    	return rows.size();
    }
    
    /*
     * returns number of columns (ie. atomics) in truth table
     */
    public Integer width() {
    	return atomics.size();
    }
    

    private boolean isEmpty() {
    	return rows.size()==1; // an empty truth table will have one row, that is a tautology (see constructor for empty truth table)
    }
    
    /**
     * Returns all rows of the truth table that evaluate to the given evaluation
     * With each row containing a list of the respective atomic/inversed atomics depending on whether the atomic evaluation is true/false in that row
     * which all must be satisfied
     */
    public List<List<AtomicExpression<?>>> evaluates_to(Boolean desiredEval){
    	
    	List<List<AtomicExpression<?>>> atomicRows = new ArrayList<>();
    	for (int i=0; i<this.height(); i++) {
    		if (evals.get(i)==desiredEval) {
    			
    			List<AtomicExpression<?>> trueAtomics = new ArrayList<>();
    			for (int j=0; j<this.width(); j++){ // iterate through columns
    				if (rows.get(i).get(j)== true) {
    					trueAtomics.add(atomics.get(j)); // use original if atomic evaluation for this row is true
    				}
    				else {
    					trueAtomics.add(inverse_atomics.get(j)); // use inverse if atomic evaluation for this row is false
    				}
    			}
    			atomicRows.add(trueAtomics);
    		}
    	}
    	return atomicRows;
    }
   
    
    
    /*
     * Returns a new truth table that is the result of combining this with other table, depending
     * on the logic connector - such that a truth table for a composite condition
     * can be made from the smaller conditions' truth tables and the given connection.
     * We do this by finding all combinations of the rows in both tables, and finding the overall evaluation using the logical connector
     * between the evaluations of each row. 
     */
    public TruthTable combine(TruthTable other_table, LogicalType connector){
    	
    	// if either truth table simply is empty, return the other one
    	if (this.isEmpty()) return other_table;
    	if (other_table.isEmpty()) return this;
    	
    	// combine the atomics of each this and other to get the headers of the combined table
    	List<AtomicExpression<?>> comb_atom = new ArrayList<>();
    	comb_atom.addAll(this.atomics);
    	comb_atom.addAll(other_table.atomics);
    	TruthTable new_table = new TruthTable(comb_atom);
    	
    	// iterate through the rows of this
    	for (int i=0; i<this.height(); i++) {
    		// iterate through the rows of other
    		for (int j=0; j<other_table.height(); j++) {
    			
    			// create the row that is the result of combining rows at this[i] with other[j]
    			List<Boolean> row = new ArrayList<>();
	    		row.addAll(rows.get(i));
	    		row.addAll(other_table.rows.get(j));
	    		
	    		// get the evaluation that is the combination of evals at this[i] and other[j], depending on the connector
	    		Boolean eval = false;
	    		switch (connector) {
	    		case AND:
	    			eval = this.evals.get(i) & other_table.evals.get(j);
	    			break;
	    		case OR:
	    			eval = this.evals.get(i) | other_table.evals.get(j);
	    			break;
//	    		case XOR:
//	    			eval = this.evals.get(i) ^ other_table.evals.get(j);
//	    			break;
	    		default:
	    			throw new RuntimeException("Cannot combine with connector: "+connector);
	    		}
	    		// add the row and eval to the combined table
	    		new_table.rows.add(row);
	    		new_table.evals.add(eval);
    		}
    	}
    	return new_table;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder("");
    	for (AtomicExpression<?> atom: atomics) {
    		result.append(atom +"\t");
    	}
		result.append("evaluation\n");
    	
		int counter = 0;
    	for (List<Boolean> row: rows) {
    		for (Boolean bool: row) {
    			result.append(bool+"\t");
    		}
    		result.append(evals.get(counter)+"\n");
    		counter+=1;
    	}
        return result.toString();
    }
}
