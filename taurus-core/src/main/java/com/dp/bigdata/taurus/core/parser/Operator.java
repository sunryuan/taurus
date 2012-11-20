package com.dp.bigdata.taurus.core.parser;

/**
 * 
 * Operator
 * @author damon.zhu
 *
 */
public class Operator {
	
	public static final char AND = '&';
	public static final char OR = '|';
	
	private char operator;
	
	public Operator(char operator){
		this.operator = operator;
	}
	
	public char getOperator(){
		return operator;
	}

}
