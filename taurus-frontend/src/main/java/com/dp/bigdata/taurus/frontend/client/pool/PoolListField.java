package com.dp.bigdata.taurus.frontend.client.pool;

public enum PoolListField {
	PoolId("poolId"),

  	PoolName("poolName");
    
    private String value;

    PoolListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
