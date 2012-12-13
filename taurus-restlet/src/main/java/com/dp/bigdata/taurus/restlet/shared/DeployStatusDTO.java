package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

/**
 * 
 * DeployStatusDTO
 * @author damon.zhu
 *
 */
public class DeployStatusDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2419938708840232982L;
	
	public static final int DEPLOYING = 0;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	
	private int status;
	
	public DeployStatusDTO() {}

	public DeployStatusDTO(int s) {
		super();
		this.status = s;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
 
}
