package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

/**
 * StatusDTO
 * 
 * @author damon.zhu
 */
public class StatusDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3411878908440479259L;

    private int id;

    private String ch_status;

    private String status;

    public StatusDTO() {
        super();
    }

    public StatusDTO(int id, String ch_status, String status) {
        super();
        this.id = id;
        this.ch_status = ch_status;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCh_status() {
        return ch_status;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCh_status(String ch_status) {
        this.ch_status = ch_status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
