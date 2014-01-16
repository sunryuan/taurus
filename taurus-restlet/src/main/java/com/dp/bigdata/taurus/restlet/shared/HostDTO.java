package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.HeartbeatInfo;

/**
 * HostDTO
 * 
 * @author damon.zhu
 */
public class HostDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5954876404845950519L;

    private Integer id;

    private String name;

    private String ip;

    private Integer poolid;

    private boolean isConnected;

    private boolean isOnline;
    
    private HeartbeatInfo info;

    public HostDTO(Integer id, String name, String ip, Integer poolid, boolean isConnected, boolean status) {
        super();
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.poolid = poolid;
        this.isConnected = isConnected;
        this.isOnline = status;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPoolid() {
        return poolid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPoolid(Integer poolid) {
        this.poolid = poolid;
    }

    public HostDTO() {
        super();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean status) {
        this.isOnline = status;
    }
    
    public HeartbeatInfo getInfo() {
		return info;
	}

	public void setInfo(HeartbeatInfo info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "HostDTO [id=" + id + ", name=" + name + ", ip=" + ip
				+ ", poolid=" + poolid + ", isConnected=" + isConnected
				+ ", isOnline=" + isOnline + ", info=" + info + "]";
	}

}
