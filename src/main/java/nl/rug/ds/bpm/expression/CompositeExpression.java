package nl.rug.ds.bpm.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public CompositeExpression(LogicalType logicalType) {
		this.logicalType = logicalType;
		atomic = false;
		arguments = new ArrayList<>();
		enclosed = false;
		originalExpression = "";
	}
	
	public CompositeExpression(AtomicExpression<?> expression) {
		this.atomic = true;
		this.expression = expression;
		this.logicalType = LogicalType.OR;
		enclosed = false;
		originalExpression = "";
	}
	
	public CompositeExpression(List<CompositeExpression> arguments, LogicalType ltype) {
		this.arguments = arguments;
		this.logicalType = ltype;
		this.atomic = false;
		enclosed = false;
		originalExpression = "";
	}
	
	public void addArgument(CompositeExpression argument) {
		if (!atomic) arguments.add(argument);
	}
	
	public void addAtomicArguments(List<AtomicExpression<?>> arguments) {
		makeNonAtomic(LogicalType.OR);
		for (AtomicExpression<?> e: arguments) {
			this.arguments.add(new CompositeExpression(e));
		}
	}
	
	public void addArguments(List<CompositeExpression> arguments) {
		makeNonAtomic(LogicalType.OR);
		this.arguments.addAll(arguments);
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
	
	// This method checks whether this ALWAYS contradicts other
	@SuppressWarnings({ "rawtypes" })
	public Boolean contradicts(AtomicExpression other) {
		if (atomic) {
			return this.expression.contradicts(other);
		}
		else {
			if (logicalType.equals(LogicalType.AND)) {
				for (CompositeExpression e: arguments) {
					if (e.contradicts(other)) return true;
				}
				return false;
			}
			else if(logicalType.equals(LogicalType.OR)) {
				for (CompositeExpression e: arguments) {
					if (!e.contradicts(other)) return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean contradicts(CompositeExpression other) {
		if (other.isAtomic()) return contradicts(other.getExpression());
		
		if (atomic) {
			return other.contradicts(this);
		}
		else {
			if ((logicalType.equals(LogicalType.OR)) && (other.getType().equals(LogicalType.OR))) {
				for (CompositeExpression e: other.getArguments()) {
					if (!contradicts(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.OR))) {
				for (CompositeExpression e: other.getArguments()) {
					if (!contradicts(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.OR)) && (other.getType().equals(LogicalType.AND))) {
				Boolean partial;
				for (CompositeExpression a: arguments) {
					partial = false;
					for (CompositeExpression e: other.getArguments()) {
						if (a.contradicts(e)) partial = true;
					}
					if (partial == false) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.AND))) {
				for (CompositeExpression e: other.getArguments()) {
					if (contradicts(e)) return true;
				}
				return false;
			}
		}
		return false;
	}

	// This method checks whether this SOMETIMES contradicts other
	// This function is asymmetric:
	// it checks whether the range of values in "other" can potentially be outside the domain of values allowed by "this"
	@SuppressWarnings({ "rawtypes" })
	public Boolean canContradict(AtomicExpression other) {		
		if (atomic) {
			return this.expression.canContradict(other);
		}
		else {
			if (logicalType.equals(LogicalType.AND)) {
				for (CompositeExpression e: arguments) {
					if (e.canContradict(other)) return true;
				}
				return false;
			}
			else if (logicalType.equals(LogicalType.OR)) {
				for (CompositeExpression e: arguments) {
					if (!e.canContradict(other)) return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean canContradict(CompositeExpression other) {
		if (other.isAtomic()) return canContradict(other.getExpression());
		
		if (atomic) {
			return other.canContradict(this);
		}
		else {
			if ((logicalType.equals(LogicalType.OR)) && (other.getType().equals(LogicalType.OR))) {
				for (CompositeExpression e: other.getArguments()) {
					if (!canContradict(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.OR))) {
				for (CompositeExpression e: other.getArguments()) {
					if (!canContradict(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.OR)) && (other.getType().equals(LogicalType.AND))) {
				Boolean partial;
				for (CompositeExpression a: arguments) {
					partial = false;
					for (CompositeExpression e: other.getArguments()) {
						if (a.canContradict(e)) partial = true;
					}
					if (partial == false) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.AND))) {
				for (CompositeExpression e: other.getArguments()) {
					if (canContradict(e)) return true;
				}
				return false;
			}
		}
		
		return false;
	}
	
	// This function checks whether this is being fulfilled by other
	// That is, other sets the condition of this to true
	@SuppressWarnings("rawtypes")
	public Boolean isFulfilledBy(AtomicExpression other) {
		if (atomic) return this.expression.isFulfilledBy(other);
		
		return false;
	}
	
	// This function checks whether this is being fulfilled by other
	// That is, other sets the condition of this to true
	public Boolean isFulfilledBy(CompositeExpression other) {
		if (!contradicts(other)) {
			Set<String> varnames = getVariableNames();
			varnames.removeAll(other.getVariableNames());
			return (varnames.size() == 0);
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
