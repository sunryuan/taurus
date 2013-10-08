package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.UserGroupMapper;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMappingMapper;
import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.generated.module.UserGroup;
import com.dp.bigdata.taurus.generated.module.UserGroupExample;
import com.dp.bigdata.taurus.generated.module.UserGroupMapping;
import com.dp.bigdata.taurus.generated.module.UserGroupMappingExample;
import com.dp.bigdata.taurus.restlet.resource.IUserResource;
import com.dp.bigdata.taurus.restlet.shared.UserDTO;

/**
 * UsersResource url : http://xxx/api/user/{user_name}
 * 
 * @author damon.zhu
 */
public class UserResource extends ServerResource implements IUserResource {
	
    private static final Log LOG = LogFactory.getLog(TaskResource.class);

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserGroupMapper groupMapper;
	
	@Autowired
	private UserGroupMappingMapper mappingMapper;

	private static final String USER_ID="id";
	private static final String TEL="tel";
	private static final String EMAIL="email";
	private static final String GROUP="groupName";
	private static final String NAME="userName";
	
	@Get
	@Override
	public boolean hasRegister() {
		String userName = (String) getRequest().getAttributes().get("user_name");
		UserExample example = new UserExample();
		example.or().andNameEqualTo(userName);
		List<User> users = userMapper.selectByExample(example);
		if (users.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Post
	@Override
	public void update(Representation re) {
		try{ 
			updateInternal(re);
		} catch(Exception e){
			LOG.error(e,e);
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		
	}
	
	private void updateInternal(Representation re){
		
		Form form = new Form(re);
		Map<String, String> formMap = new HashMap<String, String>(form.getValuesMap());
		UserDTO user = new UserDTO();
		String groupName = "";
		
		if (re == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		
		for (Entry<String, String> entry : formMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue() == null ? "" : entry.getValue().trim();
			if(key.equals(USER_ID)){
				user.setId(Integer.parseInt(value));
			} else if(key.equals(EMAIL)){
				user.setMail(value);
			} else if(key.equals(TEL)){
				user.setTel(value);
			} else if(key.equals(GROUP)){
				user.setGroup(value);
				groupName = value;
			} else if(key.equals(NAME)){
				user.setName(value);
			}
		}
		
		userMapper.updateByPrimaryKey(user.getUser());
		if(groupName !=null && !"".equals(groupName)){
			int groupID = addGroup(groupName);
			addUserGroupMapping(groupID,user.getId());
		}
	}
	
	private synchronized void addUserGroupMapping(int groupID, int userID) {
		UserGroupMappingExample example = new UserGroupMappingExample();
		example.or().andUseridEqualTo(userID);
		List<UserGroupMapping> mappings = mappingMapper.selectByExample(example);
		int size = mappings.size();
		if(size == 0){
			UserGroupMapping userGroup = new UserGroupMapping();
			userGroup.setGroupid(groupID);
			userGroup.setUserid(userID);
			mappingMapper.insert(userGroup);
		} else if(size == 1){
			UserGroupMapping userGroup = new UserGroupMapping();
			userGroup.setId(mappings.get(0).getId());
			userGroup.setGroupid(groupID);
			userGroup.setUserid(userID);
			mappingMapper.updateByPrimaryKey(userGroup);
		} else {
			throw new RuntimeException("Found user " + userID + " belongs to diffrent groups");
		} 
	}

	private synchronized int addGroup(String groupName){
		UserGroupExample groupExample = new UserGroupExample();
		groupExample.or().andGroupnameEqualTo(groupName);
		List<UserGroup> groups = groupMapper.selectByExample(groupExample);
		int size = groups.size();
		if(size == 0){
			UserGroup group = new UserGroup();
			group.setGroupname(groupName);
			groupMapper.insert(group);
			groups = groupMapper.selectByExample(groupExample);
			return groups.get(0).getId();
		} else if(size == 1){
			return groups.get(0).getId();
		} else {
			throw new RuntimeException("Found duplicated group name in table TaurusUserGroup");
		}
	}

}
