package nl.rug.ds.bpm.expression;

import java.util.ArrayList;

public class ExpressionBuilder {

	public static CompositeExpression parseExpression(String expression) {
		CompositeExpression exp;
		
		expression = expression.trim();
		
		if (expression.startsWith("(")) {
			int right = getMatchingBracket(expression, 0);
			if (right == -1) return new CompositeExpression(new ArrayList<CompositeExpression>(), LogicalType.OR);
			
			if (right == expression.length() - 1) {
				exp = parseExpression(expression.substring(1, right));
				exp.setEnclosed(true);
			}
			else {
				int flt = firstLogicalType(expression, right);
				
				LogicalType lt;
				if (flt > -1) {
					lt = getLogicalTypeAt(expression, flt);
					exp = new CompositeExpression(new ArrayList<CompositeExpression>(), lt);
					exp.addArgument(parseExpression(expression.substring(1, right)));
					
					CompositeExpression second = parseExpression(expression.substring(flt + 2));
					if (second.isAtomic()) {
						exp.addArgument(second);
					}
					else if ((lt == LogicalType.AND) && (second.getType() == LogicalType.OR)) {
						if (second.isEnclosed()) {
							exp.addArgument(second);
						}
						else {
							exp.addArgument(second.getArguments().get(0));
							second.getArguments().remove(0);
							second.getArguments().add(0, exp);
							second.setOriginalExpression(expression);
							return second;
						}
					}
					else if ((lt == LogicalType.OR) && (second.getType() == LogicalType.AND)) {
						exp.addArgument(second);
					}
					else {
						exp.addArguments(second.getArguments());
					}
				}
				else { // no logicaltype, so it's an atomic expression
					exp = new CompositeExpression(parseAtomicExpression(expression));
					return exp;
				}
			}
		}
		else { // no starting bracket
			int flt = firstLogicalType(expression, 0);
			
			if (flt > -1) { // if it has a logical operator
				LogicalType lt = getLogicalTypeAt(expression, flt);
				exp = new CompositeExpression(new ArrayList<CompositeExpression>(), lt);
				
				exp.addArgument(new CompositeExpression(parseAtomicExpression(expression.substring(0, flt))));
				
				CompositeExpression second = parseExpression(expression.substring(flt + 2));
				if (second.isAtomic()) {
					exp.addArgument(second);
				} 
				else if ((lt == LogicalType.AND) && (second.getType() == LogicalType.OR)) {
					if (second.isEnclosed()) {
						exp.addArgument(second);
					}
					else {
						exp.addArgument(second.getArguments().get(0));
						second.getArguments().remove(0);
						second.getArguments().add(0, exp);
						second.setOriginalExpression(expression);
						return second;	
					}
				}
				else if ((lt == LogicalType.OR) && (second.getType() == LogicalType.AND)) {
					exp.addArgument(second);
				}
				else {
					exp.addArguments(second.getArguments());
				}
			}
			else {
				exp = new CompositeExpression(parseAtomicExpression(expression));
			}
		}
		
		exp.setOriginalExpression(expression);
		return exp;
		
	}
	
	private static AtomicExpression<?> parseAtomicExpression(String expression) {
		String operator = getOperator(expression);
		String name;
		if (operator.equals("")) {
			name = expression;
		}
		else {
			name = expression.substring(0, expression.indexOf(operator)).trim();
		}
		return parseAtomicExpression(name, expression);
	}
	
	private static AtomicExpression<?> parseAtomicExpression(String variablename, String expression) {	
		String operator;
		ExpressionType et;
		AtomicExpression<?> exp;

		if (variablename.equals(expression)) {
			et = ExpressionType.EQ;
			
			if (expression.startsWith("!")) {
				variablename = variablename.substring(1);				
				exp = new AtomicExpression<Boolean>(variablename, et, false);
			}
			else {
				exp = new AtomicExpression<Boolean>(variablename, et, true);
			}
			return exp;
		}
		
		expression = expression.replace(variablename, "").trim();
		operator = getOperator(expression);

		switch (operator) {
		case "==":
			et = ExpressionType.EQ; break;
		case "!=":
			et = ExpressionType.NEQ; break;
		case ">":
			et = ExpressionType.GT; break;
		case ">=":
			et = ExpressionType.GEQ; break;
		case "<":
			et = ExpressionType.LT; break;
		case "<=":
			et = ExpressionType.LEQ; break;
		default:
			et = ExpressionType.NEQ;
		}
		
		expression = expression.replace(operator, "").trim();
		if (expression.isEmpty()) {
			exp = null;
		}
		else if (isNumeric(expression)) {
			exp = new AtomicExpression<Double>(variablename, et, Double.parseDouble(expression));
		}
		else if (expression.toLowerCase().equals("false") || expression.toLowerCase().equals("true")) {
			exp = new AtomicExpression<Boolean>(variablename, et, Boolean.parseBoolean(expression.toLowerCase()));
		}
		else {
			exp = new AtomicExpression<String>(variablename, et, expression);
		}
		
		return exp;
	}
	
	private static int getMatchingBracket(String expression, int left) {
		int lvl = 1;
		
		if (left == -1) {
			return -1;
		}
		else {
			int cur = left;
			while ((lvl > 0) && (cur < expression.length() - 1)) {
				cur++;
				if (expression.charAt(cur) == '(') lvl++;
				if (expression.charAt(cur) == ')') lvl--;
			}
			if (lvl > 0) return -1;
			
			return cur;
		}
	}
	
	private static int firstLogicalType(String expression, int start) {
		int pos = Integer.MAX_VALUE;
		int tp;
		
		tp = expression.indexOf("||", start);
		if ((tp >= 0) && (tp < pos)) pos = tp;
		tp = expression.indexOf("&&", start);
		if ((tp >= 0) && (tp < pos)) pos = tp;
		
		if (pos == Integer.MAX_VALUE) pos = -1;
		return pos;
	}
	
	private static LogicalType getLogicalTypeAt(String expression, int pos) {
		if (expression.length() < (pos + 2)) return LogicalType.OR;
		
		String lt = expression.substring(pos, pos + 2);
		if (lt.equals("&&")) {
			return LogicalType.AND;
		}
		else {
			return LogicalType.OR;
		}
	}
	
	private static String getOperator(String expression) {
		if (expression.contains("==")) return "==";
		if (expression.contains("!=")) return "!=";
		if (expression.contains(">=")) return ">=";
		if (expression.contains(">")) return ">";
		if (expression.contains("<=")) return "<=";
		if (expression.contains("<")) return "<";
		
		return "";
	}
	
	private static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
