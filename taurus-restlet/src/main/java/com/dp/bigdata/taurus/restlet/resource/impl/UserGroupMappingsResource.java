package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.UserGroupMappingMapper;
import com.dp.bigdata.taurus.generated.module.UserGroupMapping;
import com.dp.bigdata.taurus.generated.module.UserGroupMappingExample;
import com.dp.bigdata.taurus.restlet.resource.IUserGroupMappingsResource;
import com.dp.bigdata.taurus.restlet.shared.UserGroupMappingDTO;

/**
 * UserGroupMappingsResource url : http://xxx/api/usergroup
 * 
 * @author renyuan.sun
 */
public class UserGroupMappingsResource extends ServerResource implements IUserGroupMappingsResource {
	@Autowired
	private UserGroupMappingMapper userGroupMappingMapper;

	@Override
	@Get
	public ArrayList<UserGroupMappingDTO> retrieve() {
		UserGroupMappingExample example = new UserGroupMappingExample();
		example.or();
		List<UserGroupMapping> userGroups = userGroupMappingMapper.selectByExample(example);
		ArrayList<UserGroupMappingDTO> userGroupDtos = new ArrayList<UserGroupMappingDTO>();
		for (UserGroupMapping userGroup : userGroups) {
			UserGroupMappingDTO userGroupDto = new UserGroupMappingDTO();
			userGroupDto.setId(userGroup.getId());
			userGroupDto.setUserId(userGroup.getUserid());
			userGroupDto.setGroupId(userGroup.getGroupid());
			userGroupDtos.add(userGroupDto);
		}
		return userGroupDtos;
	}
}
