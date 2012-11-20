package com.dp.bigdata.taurus.frontend.client.pool;

public enum HostListField {
  	HostId("hostId"),
	
  	HostIp("hostIp"),
  	  	
  	PoolName("poolName"),
  	
  	IsConnected("isConnected");

    
    private String value;

    HostListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
