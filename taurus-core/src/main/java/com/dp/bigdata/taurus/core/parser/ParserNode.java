package com.dp.bigdata.taurus.core.parser;

/**
 * 
 * @author damon.zhu
 *
 */
public class ParserNode {
	
	private boolean isOperator;
	private Operation operation;
	private Operator operator;
	private ParserNode next;
	
	public ParserNode(Operator operator) {
		super();
		isOperator = true;
		this.operator = operator;
	}
	
	public ParserNode(Operation operation){
		isOperator = false;
		this.operation = operation;
	}
	
	public boolean isOperator() {
		return isOperator;
	}

	public void setOperator(boolean isOperator) {
		this.isOperator = isOperator;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public ParserNode getNext() {
		return next;
	}

	public void setNext(ParserNode next) {
		this.next = next;
	}

}
