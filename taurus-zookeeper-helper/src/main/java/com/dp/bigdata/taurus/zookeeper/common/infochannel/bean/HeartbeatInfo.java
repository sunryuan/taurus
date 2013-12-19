package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;
import java.util.Date;

public class HeartbeatInfo implements Serializable {
    
    private static final long serialVersionUID = 5912013362307439731L;
    
    private String agentVersion;
    private String configs;
    private String user;
    private boolean isLinux;
    private Date time;
    private boolean needUpdate;

    /**
     * @return the agentVersion
     */
    public String getAgentVersion() {
        return agentVersion;
    }

    /**
     * @return the isLinux
     */
    public boolean isLinux() {
        return isLinux;
    }

    /**
     * @param isLinux the isLinux to set
     */
    public void setLinux(boolean isLinux) {
        this.isLinux = isLinux;
    }

    /**
     * @return the configs
     */
    public String getConfigs() {
        return configs;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param agentVersion the agentVersion to set
     */
    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    /**
     * @param configs the configs to set
     */
    public void setConfigs(String configs) {
        this.configs = configs;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	@Override
	public String toString() {
		return "HeartbeatInfo [agentVersion=" + agentVersion + ", configs="
				+ configs + ", user=" + user + ", isLinux=" + isLinux
				+ ", time=" + time + ", needUpdate=" + needUpdate + "]";
	}
    
}
