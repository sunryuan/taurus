package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

public class UserGroupMappingDTO implements Serializable {
	
	private static final long serialVersionUID = -3438233560556915120L;
	private int id;
	private int groupId;
	private int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
