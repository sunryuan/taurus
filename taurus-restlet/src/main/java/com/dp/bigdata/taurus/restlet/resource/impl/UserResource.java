package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.List;

import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.restlet.resource.IUserResource;

/**
 * UsersResource url : http://xxx/api/user/{user_name}
 * 
 * @author damon.zhu
 */
public class UserResource extends ServerResource implements IUserResource {

	@Autowired
	private UserMapper userMapper;

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

}
