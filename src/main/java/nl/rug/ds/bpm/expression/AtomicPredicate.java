package nl.rug.ds.bpm.expression;

public class AtomicPredicate<T extends Comparable<T>> implements AtomicExpression<T>{
	private T value;
	private String variablename;
	private String originalExpression;
	private ExpressionType type;

	public AtomicPredicate(String variablename, ExpressionType type, T value) {
		this.variablename = variablename;
		this.type = type;
		this.value = value;
		this.originalExpression = "";
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

	public AtomicPredicate<T> negate() {
		ExpressionType negated;
		switch (type) {
		case EQ:
			negated = ExpressionType.NEQ; break;
		case NEQ:
			negated = ExpressionType.EQ; break;
		case LT:
			negated = ExpressionType.GEQ; break;
		case LEQ:
			negated = ExpressionType.GT; break;
		case GT:
			negated = ExpressionType.LEQ; break;
		case GEQ:
			negated = ExpressionType.LT; break;
		default:
			throw new RuntimeException("Cannot negate: "+toString());
		}
		return new AtomicPredicate<T>(variablename, negated, value);
	}

	public String getVariableName() {
		return variablename;
	}

	public void setOriginalExpression(String originalExpression) {
		this.originalExpression = originalExpression;
	}

	public String getOriginalExpression() {
		return originalExpression;
	}
}
