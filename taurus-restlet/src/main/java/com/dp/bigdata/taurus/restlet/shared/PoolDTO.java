package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

/**
 * 
 * PoolDTO
 * @author damon.zhu
 *
 */
public class PoolDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2924050604483814690L;
	// id = 1 means Unallocated
	private int id;
	private String name;
	private String creator;

	public PoolDTO(){
		super();
	}
	
	public PoolDTO(int id, String name, String creator) {
		super();
		this.id = id;
		this.name = name;
		this.creator = creator;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
