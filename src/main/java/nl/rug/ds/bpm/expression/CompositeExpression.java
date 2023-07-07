package nl.rug.ds.bpm.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.ds.bpm.expression.solution.DisjointDomain;
import nl.rug.ds.bpm.expression.solution.TruthTable;

/**
 * Created by Nick van Beest on 1 Oct. 2018
 *
 */
public class CompositeExpression implements Comparable<CompositeExpression>{
	private LogicalType logicalType;

	private List<CompositeExpression> arguments;

	private Boolean enclosed;
	private Boolean atomic;

	private String originalExpression;

	private AtomicExpression<?> expression;
	
	private TruthTable table;

	public CompositeExpression(LogicalType logicalType) {
		this.logicalType = logicalType;
		atomic = false;
		arguments = new ArrayList<>();
		enclosed = false;
		originalExpression = "";
		this.table = new TruthTable();
	}

	public CompositeExpression(AtomicExpression<?> expression) {
		this.atomic = true;
		this.expression = expression;
		this.logicalType = LogicalType.OR;
		enclosed = false;
		originalExpression = "";
		this.table = new TruthTable(expression);
	}

	public CompositeExpression(List<CompositeExpression> arguments, LogicalType ltype) {
		this.arguments = arguments;
		this.logicalType = ltype;
		this.atomic = false;
		enclosed = false;
		originalExpression = "";
		this.table = new TruthTable();
		for (int i=0; i<arguments.size(); i++) {
			this.table = this.table.combine(arguments.get(i).table, ltype);
		}
	}

	public void addArgument(CompositeExpression argument) {
		if (!atomic) arguments.add(argument);
		this.table = this.table.combine(argument.table, logicalType);
	}

	public void addAtomicArguments(List<AtomicExpression<?>> arguments) {
		makeNonAtomic(LogicalType.OR);
		for (AtomicExpression<?> e: arguments) {
			this.arguments.add(new CompositeExpression(e));
		}
		for (int i=this.arguments.size()-arguments.size()-1; i<this.arguments.size(); i++) {
			this.table = this.table.combine(this.arguments.get(i).table, LogicalType.OR);
		}
	}

	public void addArguments(List<CompositeExpression> arguments) {
		makeNonAtomic(LogicalType.OR);
		this.arguments.addAll(arguments);
		for (int i=0; i<arguments.size(); i++) {
			this.table = this.table.combine(arguments.get(i).table, LogicalType.OR);
		}
	}
	
	/*
	 * returns the complement of the condition
	 */
	public CompositeExpression negate() {
		if (atomic) return new CompositeExpression(expression.negate());
		else {
			List<CompositeExpression> negatedArguments = arguments.stream().map(a->a.negate()).toList();
			switch (logicalType) {
			case AND:
				return new CompositeExpression(negatedArguments, LogicalType.OR);
			case OR:
				return new CompositeExpression(negatedArguments, LogicalType.AND);
			default:
				throw new RuntimeException("Cannot negate: "+this.toString());
			}
		}
	}

	public List<CompositeExpression> getArguments() {
		return arguments;
	}

	public void setType(LogicalType logicalType) {
		this.logicalType = logicalType;
	}

	public LogicalType getType() {
		return logicalType;
	}

	public Boolean isAtomic() {
		return atomic;
	}
	
	public TruthTable getTable() {
		return table;
	}

	public void makeNonAtomic(LogicalType logicalType) {
		if (atomic) {
			this.logicalType = logicalType;
			this.arguments = new ArrayList<CompositeExpression>();
			arguments.add(new CompositeExpression(expression));
			this.atomic = false;
		}
	}

	public boolean isEnclosed() {return enclosed;}

	public void setEnclosed(boolean enclosed) {
		this.enclosed = enclosed;
	}

	public AtomicExpression<?> getExpression() {
		if (atomic) return expression;

		return null;
	}

	public Boolean contradicts(CompositeExpression other) {
		CompositeExpression conjunction = new CompositeExpression(List.of(this, other), LogicalType.AND);
		DisjointDomain solution = new DisjointDomain().evaluate(conjunction);
		return solution.isEmpty();
	}

	// This method checks whether this SOMETIMES contradicts other
	// This function is asymmetric:
	// it checks whether the range of values in "other" can potentially be outside the domain of values allowed by "this"
	public Boolean canBeContradictedBy(CompositeExpression other) {
		CompositeExpression conjunction = new CompositeExpression(List.of(this.negate(), other), LogicalType.AND);
		DisjointDomain solution = new DisjointDomain().evaluate(conjunction);
		return !solution.isEmpty();
	}

	// This function checks whether this is being fulfilled by other
	// That is, other sets the condition of this to true
	// This function is asymmetric
	public Boolean isFulfilledBy(CompositeExpression other) {
		if (!this.canBeContradictedBy(other)) {
			return other.getVariableNames().containsAll(getVariableNames());
		}
		return false;
	}

	public Boolean containsVariable(String varname) {
		if (atomic) {
			return this.expression.getVariableName().equals(varname);
		}
		else {
			for (CompositeExpression e: arguments) {
				if (e.containsVariable(varname)) return true;
			}
		}

		return false;
	}

	public Set<String> getVariableNames() {
		Set<String> varnames = new HashSet<String>();

		if (atomic) {
			varnames.add(expression.getVariableName());
		}
		else {
			for (CompositeExpression e: arguments) {
				varnames.addAll(e.getVariableNames());
			}
		}

		return varnames;
	}

	public void setOriginalExpression(String originalExpression) {
		this.originalExpression = originalExpression;
	}

	public String getOriginalExpression() {
		return originalExpression;
	}

	@Override
	public String toString() {
		String ex = "";

		if (atomic) {
			return "(" + expression.toString() + ")";
		}
		else {
			ex = "(";
			for (CompositeExpression e: arguments) {
				ex += e.toString();
				if (logicalType.equals(LogicalType.AND)) ex += " && ";
				if (logicalType.equals(LogicalType.OR)) ex += " || ";
			}

			if (arguments.size() > 0) ex = ex.substring(0, ex.length() - 4);
			ex += ")";
		}

		return ex;
	}

	@Override
	public int compareTo(CompositeExpression o) {
		if (this.atomic) {
			if (o.isAtomic()) {
				return (this.toString().compareTo(o.toString()));
			}
			else {
				return -1;
			}
		}
		else if (o.isAtomic()) {
			return 1;
		}
		else if (this.logicalType.equals(o.logicalType)) {
			if (this.arguments.size() != o.getArguments().size()) {
				return (this.arguments.size() < o.getArguments().size()) ? -1 : 1;
			}
			else {
				int ret = 0;
				for (int  i = 0; i < this.arguments.size(); i++) {
					ret = (this.getArguments().get(i).compareTo(o.getArguments().get(i)));
					if (ret != 0) return ret;
				}
				return ret;
			}
		}
		else {
			return (this.logicalType.equals(LogicalType.AND) ? -1 : 1);
		}

		//		return this.toString().compareTo(o.toString());
	}
}
