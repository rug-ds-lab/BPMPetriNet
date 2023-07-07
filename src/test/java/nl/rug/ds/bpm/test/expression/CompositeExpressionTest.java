package nl.rug.ds.bpm.test.expression;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.rug.ds.bpm.expression.ExpressionBuilder;

/*
 * Created by Hannah Burke on 22 June 2023
 */
class CompositeExpressionTest {

	@Test
	void contradicts() {
		assertTrue(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("x<3")));
		assertFalse(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("x>4")));
		assertTrue(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("x==2")));

		assertTrue(ExpressionBuilder.parseExpression("x<3 || x==2").contradicts(ExpressionBuilder.parseExpression("x>3")));
		assertTrue(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("x<3 || x==2")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 && x==2").contradicts(ExpressionBuilder.parseExpression("x>2")));
		assertTrue(ExpressionBuilder.parseExpression("x>2").contradicts(ExpressionBuilder.parseExpression("x<3 && x==2")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 || x<3").contradicts(ExpressionBuilder.parseExpression("x>3 || x>3")));

		assertTrue(ExpressionBuilder.parseExpression("x<3 || x==2").contradicts(ExpressionBuilder.parseExpression("x>3 || x==5")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 && x==2").contradicts(ExpressionBuilder.parseExpression("x>3 && x==5")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 && x>1").contradicts(ExpressionBuilder.parseExpression("x>3 || x<1")));
		assertTrue(ExpressionBuilder.parseExpression("x<3 || x>1").contradicts(ExpressionBuilder.parseExpression("x>3 && x<1")));
		assertFalse(ExpressionBuilder.parseExpression("x<3 || x>1").contradicts(ExpressionBuilder.parseExpression("x>3 || x<1")));

		assertFalse(ExpressionBuilder.parseExpression("(x<3 && x==2) || x>4").
				contradicts(ExpressionBuilder.parseExpression("x>3 && x==5")));
		
		assertTrue(ExpressionBuilder.parseExpression("x==true").contradicts(ExpressionBuilder.parseExpression("x==false")));
		assertFalse(ExpressionBuilder.parseExpression("x==false").contradicts(ExpressionBuilder.parseExpression("x==false")));
		assertFalse(ExpressionBuilder.parseExpression("x==true").contradicts(ExpressionBuilder.parseExpression("(x==false || x==true)")));
	}
	
	@Test
	void contradictsMultiVar() {
		assertFalse(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("y<3")));
		assertFalse(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("y>3")));
		
		assertFalse(ExpressionBuilder.parseExpression("x>3 && y>3").contradicts(ExpressionBuilder.parseExpression("(x<3 && y<3) || (x>3 && y>3)")));
		assertTrue(ExpressionBuilder.parseExpression("x>3 && y<3").contradicts(ExpressionBuilder.parseExpression("(x<3 && y<3) || (x>3 && y>3)")));
		assertFalse(ExpressionBuilder.parseExpression("x>3 && y<3").contradicts(ExpressionBuilder.parseExpression("(x<3 || y<3) && (x>3 || y>3)")));
		assertTrue(ExpressionBuilder.parseExpression("x>3 && y>3").contradicts(ExpressionBuilder.parseExpression("(x<3 || y<3) && (x>3 || y>3)")));
		
		assertFalse(ExpressionBuilder.parseExpression("x>3").contradicts(ExpressionBuilder.parseExpression("y==true")));
	}
	
	@Test
	void canContradict() {
				
		assertTrue(ExpressionBuilder.parseExpression("x<5").canBeContradictedBy(ExpressionBuilder.parseExpression("x>3")));
		assertFalse(ExpressionBuilder.parseExpression("x>3").canBeContradictedBy(ExpressionBuilder.parseExpression("x>4")));
		assertTrue(ExpressionBuilder.parseExpression("x>4").canBeContradictedBy(ExpressionBuilder.parseExpression("x>3")));
		assertTrue(ExpressionBuilder.parseExpression("x==2").canBeContradictedBy(ExpressionBuilder.parseExpression("x>1")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 || x==4").canBeContradictedBy(ExpressionBuilder.parseExpression("x>3")));
		assertTrue(ExpressionBuilder.parseExpression("x>3").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 || x==4")));
		
		assertTrue(ExpressionBuilder.parseExpression("x<3 && x>1").canBeContradictedBy(ExpressionBuilder.parseExpression("x>2")));
		assertTrue(ExpressionBuilder.parseExpression("x>2").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 && x>1")));
		
		assertFalse(ExpressionBuilder.parseExpression("x<3").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 && x>1")));
		assertFalse(ExpressionBuilder.parseExpression("x<3 && x>1").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 && x>1")));

		assertFalse(ExpressionBuilder.parseExpression("x==2").canBeContradictedBy(ExpressionBuilder.parseExpression("x<=2 && x>=2")));
		assertFalse(ExpressionBuilder.parseExpression("x>=2").canBeContradictedBy(ExpressionBuilder.parseExpression("x<=2 && x>=2")));

		assertTrue(ExpressionBuilder.parseExpression("x<3 || x<3").canBeContradictedBy(ExpressionBuilder.parseExpression("x>3 || x>3")));
		assertFalse(ExpressionBuilder.parseExpression("x<3 || x>5").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 || x>5")));

		assertFalse(ExpressionBuilder.parseExpression("x<3 || x>5").canBeContradictedBy(ExpressionBuilder.parseExpression("x>6 || x<2")));

		assertTrue(ExpressionBuilder.parseExpression("x<3 && x==2").canBeContradictedBy(ExpressionBuilder.parseExpression("x>3 && x==5")));
		assertFalse(ExpressionBuilder.parseExpression("x<4 && x>0").canBeContradictedBy(ExpressionBuilder.parseExpression("x<3 && x>1")));
		
		assertTrue(ExpressionBuilder.parseExpression("x==true").canBeContradictedBy(ExpressionBuilder.parseExpression("x==false")));
		assertFalse(ExpressionBuilder.parseExpression("x==false").canBeContradictedBy(ExpressionBuilder.parseExpression("x==false")));
		assertTrue(ExpressionBuilder.parseExpression("x==true").canBeContradictedBy(ExpressionBuilder.parseExpression("(x==false || x==true)")));
	}
	
	@Test
	void fulfills() {
		assertTrue(ExpressionBuilder.parseExpression("x>1").isFulfilledBy(ExpressionBuilder.parseExpression("x>2")));
		assertFalse(ExpressionBuilder.parseExpression("x>2").isFulfilledBy(ExpressionBuilder.parseExpression("x>1")));
		assertFalse(ExpressionBuilder.parseExpression("x>1 && y==0").isFulfilledBy(ExpressionBuilder.parseExpression("x>2")));


	}

}
