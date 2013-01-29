package com.dp.bigdata.taurus.core.parser;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

/**
 * 
 * DependencyParserTest
 * @author damon.zhu
 *
 */
public class DependencyParserTest {

	@Test
	public void testPrefixExpression1() throws ParseException {
		
		String dependencyExpr = "[wordcount][10][0]&[LogSort][10][0]|[HipSort][10][0]&[test][1][0]";
		ParserNode current = DependencyParser.postExpression(dependencyExpr);
		
		assertEquals("wordcount",current.getOperation().getName());
		current = current.getNext();
		assertEquals("LogSort",current.getOperation().getName());
		current = current.getNext();
		assertEquals('&',current.getOperator().getOperator());
		current = current.getNext();
		assertEquals("HipSort",current.getOperation().getName());
		current = current.getNext();
	    assertEquals("test",current.getOperation().getName());
        current = current.getNext();
        assertEquals('&',current.getOperator().getOperator());
        current = current.getNext();
		assertEquals('|',current.getOperator().getOperator());
		current = current.getNext();
		assertEquals(null,current);
	}

	@Test
	public void testPrefixExpression2() throws ParseException {

		String dependencyExpr = "[wordcount][10][0]|[LogSort][10][0]&[HipSort][10][0]";
		ParserNode current = DependencyParser.postExpression(dependencyExpr);
		
		assertEquals("wordcount",current.getOperation().getName());
		current = current.getNext();
		assertEquals("LogSort",current.getOperation().getName());
		current = current.getNext();
		assertEquals("HipSort",current.getOperation().getName());
		current = current.getNext();
		assertEquals('&',current.getOperator().getOperator());
		current = current.getNext();
		assertEquals('|',current.getOperator().getOperator());
		current = current.getNext();
		assertEquals(null,current);
	}
	
	@Test
	public void testPrefixExpression3() throws ParseException {

        String dependencyExpr = "[rtable-(dai.ly)][1][0]";
		ParserNode current = DependencyParser.postExpression(dependencyExpr);
		
        assertEquals("rtable-(dai.ly)", current.getOperation().getName());
		current = current.getNext();
		assertEquals(null,current);
	}
}
