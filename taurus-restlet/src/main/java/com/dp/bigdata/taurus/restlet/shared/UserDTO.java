package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * UserDTO
 * @author damon.zhu
 *
 */
@XStreamAlias("user")
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 8473964829047181355L;
	private String name;
	private String password;
	
	public UserDTO(){}
	
	public UserDTO(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
}
