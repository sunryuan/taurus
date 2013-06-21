package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.restlet.resource.IUsersResource;
import com.dp.bigdata.taurus.restlet.shared.UserDTO;

/**
 * UsersResource url : http://xxx/api/user
 * 
 * @author damon.zhu
 */
public class UsersResource extends ServerResource implements IUsersResource {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Get
    public ArrayList<UserDTO> retrieve() {
        UserExample example = new UserExample();
        example.or();
        List<User> users = userMapper.selectByExample(example);
        ArrayList<UserDTO> userDtos = new ArrayList<UserDTO>();
        for (User user : users) {
            userDtos.add(new UserDTO(user.getId(), user.getName(), user.getMail(), user.getTel()));
        }
        return userDtos;
    }

	@Override
   public void createIfNotExist(UserDTO user) {
		UserExample example = new UserExample();
		example.or().andNameEqualTo(user.getName());
		List<User> userDtos = userMapper.selectByExample(example);
		if(userDtos == null || userDtos.size() == 0){
			User usr = new User();
			usr.setName(user.getName());
			usr.setMail(user.getMail());
			
			userMapper.insertSelective(usr);
		}
   }

}
