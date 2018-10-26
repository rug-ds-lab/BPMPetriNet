package nl.rug.ds.bpm.expression;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nick van Beest on 1 Oct. 2018
 *
 */
public class ExpressionConn {
	private LogicalType logicalType;
	
	private Set<ExpressionConn> arguments;
	
	private Boolean atomic;
	
	private Expression<?> expression;
	
	public ExpressionConn(Expression<?> expression) {
		this.atomic = true;
		this.expression = expression;
		this.logicalType = LogicalType.XOR;
	}
	
	public ExpressionConn(Set<ExpressionConn> arguments, LogicalType ltype) {
		this.arguments = arguments;
		this.logicalType = ltype;
		this.atomic = false;
	}
	
	public void addArgument(ExpressionConn argument) {
		if (!atomic) arguments.add(argument);
	}
	
	public void addAtomicArguments(Set<Expression<?>> arguments) {
		makeNonAtomic(LogicalType.XOR);
		for (Expression<?> e: arguments) {
			this.arguments.add(new ExpressionConn(e));
		}
	}
	
	public void addArguments(Set<ExpressionConn> arguments) {
		makeNonAtomic(LogicalType.XOR);
		this.arguments.addAll(arguments);
	}
	
	public Set<ExpressionConn> getArguments() {
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
			this.arguments = new HashSet<ExpressionConn>();
			arguments.add(new ExpressionConn(expression));
			this.atomic = false;
		}
	}
	
	public Expression<?> getExpression() {
		if (atomic) return expression;
		
		return null;
	}
	
	// This method checks whether this ALWAYS contradicts other
	@SuppressWarnings({ "rawtypes" })
	public Boolean contradicts(Expression other) {
		if (atomic) {
			return this.expression.contradicts(other);
		}
		else {
			if (logicalType.equals(LogicalType.AND)) {
				for (ExpressionConn e: arguments) {
					if (e.contradicts(other)) return true;
				}
				return false;
			}
			else if(logicalType.equals(LogicalType.XOR)) {
				for (ExpressionConn e: arguments) {
					if (!e.contradicts(other)) return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean contradicts(ExpressionConn other) {
		if (other.isAtomic()) return contradicts(other.getExpression());
		
		if (atomic) {
			return other.contradicts(this);
		}
		else {
			if ((logicalType.equals(LogicalType.XOR)) && (other.getType().equals(LogicalType.XOR))) {
				for (ExpressionConn e: other.getArguments()) {
					if (!contradicts(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.XOR))) {
				for (ExpressionConn e: other.getArguments()) {
					if (!contradicts(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.XOR)) && (other.getType().equals(LogicalType.AND))) {
				Boolean partial;
				for (ExpressionConn a: arguments) {
					partial = false;
					for (ExpressionConn e: other.getArguments()) {
						if (a.contradicts(e)) partial = true;
					}
					if (partial == false) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.AND))) {
				for (ExpressionConn e: other.getArguments()) {
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
	public Boolean canContradict(Expression other) {		
		if (atomic) {
			return this.expression.canContradict(other);
		}
		else {
			if (logicalType.equals(LogicalType.AND)) {
				for (ExpressionConn e: arguments) {
					if (e.canContradict(other)) return true;
				}
				return false;
			}
			else if (logicalType.equals(LogicalType.XOR)) {
				for (ExpressionConn e: arguments) {
					if (!e.canContradict(other)) return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean canContradict(ExpressionConn other) {
		if (other.isAtomic()) return canContradict(other.getExpression());
		
		if (atomic) {
			return other.canContradict(this);
		}
		else {
			if ((logicalType.equals(LogicalType.XOR)) && (other.getType().equals(LogicalType.XOR))) {
				for (ExpressionConn e: other.getArguments()) {
					if (!canContradict(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.XOR))) {
				for (ExpressionConn e: other.getArguments()) {
					if (!canContradict(e)) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.XOR)) && (other.getType().equals(LogicalType.AND))) {
				Boolean partial;
				for (ExpressionConn a: arguments) {
					partial = false;
					for (ExpressionConn e: other.getArguments()) {
						if (a.canContradict(e)) partial = true;
					}
					if (partial == false) return false;
				}
				return true;
			}
			else if ((logicalType.equals(LogicalType.AND)) && (other.getType().equals(LogicalType.AND))) {
				for (ExpressionConn e: other.getArguments()) {
					if (canContradict(e)) return true;
				}
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String ex = "";
		
		if (atomic) {
			return "(" + expression.toString() + ")";
		}
		else {
			ex = "(";
			for (ExpressionConn e: arguments) {
				ex += e.toString();
				if (logicalType.equals(LogicalType.AND)) ex += " && ";
				if (logicalType.equals(LogicalType.XOR)) ex += " || ";
			}
			
			if (arguments.size() > 0) ex = ex.substring(0, ex.length() - 4);
			ex += ")";
		}
		
		return ex;
	}
}
