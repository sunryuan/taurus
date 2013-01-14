package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

/**
 * UserGroupDTO
 * 
 * @author damon.zhu
 */
public class UserGroupDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3264461236839609486L;
    private int id;
    private String name;

    public UserGroupDTO(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
