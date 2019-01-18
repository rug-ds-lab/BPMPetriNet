package nl.rug.ds.bpm.expression;

public class AtomicExpression<T extends Comparable<T>> implements Comparable<AtomicExpression<T>> {
	private T value;
	private String variablename;
	private ExpressionType type;
	
	public AtomicExpression(String variablename, ExpressionType type, T value) {
		this.variablename = variablename;
		this.type = type;
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public ExpressionType getExpressionType() {
		return type;
	}
	
	public Boolean accepts(T value) {
		if ((value.getClass() == this.value.getClass()) || 
				((value instanceof Number) && (this.value instanceof Number))) {
			switch (type) {
			case EQ:
				return (this.value.equals(value)); 
			case NEQ:
				return (!this.value.equals(value));
			case LT:
				return (value.compareTo(this.value) < 0);
			case LEQ:
				return (value.compareTo(this.value) <= 0);
			case GT:
				return (value.compareTo(this.value) > 0);
			case GEQ:
				return (value.compareTo(this.value) >= 0);
			}
		}
		
		return false;
	}
	
	// This method checks whether this ALWAYS contradicts other
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean contradicts(AtomicExpression other) {
		if (this.getVariableName().equals(other.getVariableName())) {
			if (!this.getValue().getClass().equals(other.getValue().getClass())) return true;
			
			switch (type) {
			case EQ:
				return (!other.accepts(this.value)); 
			case NEQ:
				if ((other.getExpressionType() == ExpressionType.EQ) && (other.getValue().equals(this.value))) {
					return true;
				}
				else if ((this.getValue().getClass().equals(Boolean.class)) && (other.getValue().getClass().equals(Boolean.class))) {
					if ((other.getExpressionType() == ExpressionType.NEQ) && (!other.getValue().equals(this.value))) {
						return true;
					}
				}
				return false;
			case LT:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if (other.getExpressionType() == ExpressionType.GT) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else if (other.getExpressionType() == ExpressionType.GEQ) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else {
						return ((other.getExpressionType() == ExpressionType.EQ) && (((Double)other.value).doubleValue() >= ((Double)this.value).doubleValue()));
					}
				}
			case LEQ:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if (other.getExpressionType() == ExpressionType.GT) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else if (other.getExpressionType() == ExpressionType.GEQ) {
						return (((Double) this.value).doubleValue() < ((Double) other.value).doubleValue());
					}
					else {
						return ((other.getExpressionType() == ExpressionType.EQ) && (((Double)other.value).doubleValue() > ((Double)this.value).doubleValue()));

					}
				}
			case GT:
			case GEQ:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if ((other.getExpressionType() == ExpressionType.GT) || (other.getExpressionType() == ExpressionType.GEQ)) {
						return false;
					}
					else {
						return other.contradicts(this);
					}
				}
			}
		}
		
		return false;
	}
	
	// This method checks whether this SOMETIMES contradicts other
	// This function is asymmetric:
	// it checks whether the range of values in "other" can potentially be outside the domain of values allowed by "this"
	@SuppressWarnings({ "rawtypes" })
	public Boolean canContradict(AtomicExpression other) {		
		if (this.variablename.equals(other.variablename)) {
			return !((overlaps(other) == 0) || (overlaps(other) == 1));
		}
		else {
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public int overlaps(AtomicExpression other) {
		// this function returns:
		// 0 when ranges are identical (this+other)
		// 1 when this has a larger range than other (this+other, this)
		// -1 when this has a smaller range than other (this+other, other)
		// 2 when this and other overlap (this, this+other, other)
		// -2 when this and other are mutually exclusive (this, other)
		int overlaps = 0;
		
		switch(type) {
		case EQ:
		case NEQ:
			if (!contradicts(other)) 
				return 0;
			else
				return -2;
		case GEQ:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.GEQ) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if (other.type == ExpressionType.GT) {
					if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if ((other.type == ExpressionType.EQ) || (other.type == ExpressionType.NEQ)) {
					return 1;
				}
				else {
					return 2;
				}
			}
		case GT:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.GT) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if (other.type == ExpressionType.GEQ) {
					if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if ((other.type == ExpressionType.EQ) || (other.type == ExpressionType.NEQ)) {
					return 1;
				}
				else {
					return 2;
				}
			}
		case LEQ:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.LEQ) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if (other.type == ExpressionType.LT) {
					if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if ((other.type == ExpressionType.EQ) || (other.type == ExpressionType.NEQ)) {
					return 1;
				}
				else {
					return 2;
				}
			}
		case LT:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.LT) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if (other.type == ExpressionType.LEQ) {
					if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if ((other.type == ExpressionType.EQ) || (other.type == ExpressionType.NEQ)) {
					return 1;
				}
				else {
					return 2;
				}
			}
		}
		
		return overlaps;
	}
	
	// This function checks whether this is being fulfilled by other
	// That is, other sets the condition of this to true
	@SuppressWarnings("rawtypes")
	public Boolean isFulfilledBy(AtomicExpression other) {
		if (this.variablename.equals(other.variablename)) {
			return !this.contradicts(other);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String s = variablename;
		
		switch (type) {
		case EQ: s += " == ";
			break;
		case GEQ: s += " >= ";
			break;
		case GT: s += " > ";
			break;
		case LEQ: s += " <= ";
			break;
		case LT: s += " < ";
			break;
		case NEQ: s += " != ";
		}
		
		s += value;
		
		return s;
	}
	
	public String getVariableName() {
		return variablename;
	}

	@Override
	public int compareTo(AtomicExpression<T> o) {
		int ret = (o.getExpressionType() == type ? 0 : -1);
		if (ret == 0)
			ret = o.getVariableName().compareTo(variablename);
		if (ret == 0)
			ret = (o.accepts(value) ? o.getValue().compareTo(value) : -1);
		return ret;
	}
}
