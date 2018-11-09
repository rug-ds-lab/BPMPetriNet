package nl.rug.ds.bpm.expression;

import java.util.HashSet;

public class ExpressionBuilder {

	public static CompositeExpression parseExpression(String expression) {
		CompositeExpression exp = new CompositeExpression(new HashSet<CompositeExpression>(), LogicalType.XOR);
	
		int left = expression.indexOf("(");
		int right = -1;
		
		if (left == -1) {
			exp = new CompositeExpression(parseAtomicExpression(expression));
		}
		else {
			right = getMatchingBracket(expression, left);
			if (right == -1) return exp;

			if (right == expression.length() - 1) {
				return parseExpression(expression.substring(left + 1, right));
			}
			else {
				if (expression.indexOf(" || ") == right + 1) {
					exp.setType(LogicalType.XOR);
				}
				else {
					exp.setType(LogicalType.AND);
				}
				
				exp.addArgument(parseExpression(expression.substring(left, right + 1)));
				
				left = right + 5;
				right = getMatchingBracket(expression, left);
				exp.addArgument(parseExpression(expression.substring(left, right + 1)));
			}
		}
		
		return exp;
	}
	
	public static AtomicExpression<?> parseAtomicExpression(String expression) {
		String operator = getOperator(expression);
		String name = expression.substring(0, expression.indexOf(operator)).trim();
		return parseAtomicExpression(name, expression);
	}
	
	public static AtomicExpression<?> parseAtomicExpression(String variablename, String expression) {		
		String operator;
		ExpressionType et;
		AtomicExpression<?> exp;

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
		
		if (isNumeric(expression)) {
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
