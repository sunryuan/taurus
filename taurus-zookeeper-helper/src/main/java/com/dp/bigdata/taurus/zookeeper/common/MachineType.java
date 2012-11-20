package com.dp.bigdata.taurus.zookeeper.common;

public enum MachineType {
	
	SERVER(0, "server"),
	AGENT(1, "agent");

	private int typeID;
	private String name;
	
	private MachineType(int typeID, String name){
		this.typeID = typeID;
		this.name = name;
	}
	
	public int getTypeID(){
		return typeID;
	}
	
	public String getName(){
		return name;
	}
}
