package com.dp.bigdata.taurus.restlet.shared;

/**
 * UserGroupDTO
 * 
 * @author damon.zhu
 */
public class UserGroupDTO {

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
