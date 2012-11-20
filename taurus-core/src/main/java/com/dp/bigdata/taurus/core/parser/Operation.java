package com.dp.bigdata.taurus.core.parser;

/**
 * 
 * Operation
 * @author damon.zhu
 *
 */
public class Operation {

	private String name;
	private int number;
	private int value;
	
	public Operation(String name, int number, int value) {
		super();
		this.name = name;
		this.number = number;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
