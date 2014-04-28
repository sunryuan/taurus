package com.dp.bigdata.taurus.zookeeper.deploy.helper;

import java.io.Serializable;

public class DeploymentContext implements Serializable {

   private static final long serialVersionUID = -4873184217376298170L;

	private String url;

	private String name;
	
	private String depolyId;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepolyId() {
	   return depolyId;
   }

	public void setDepolyId(String depolyId) {
	   this.depolyId = depolyId;
   }

}
