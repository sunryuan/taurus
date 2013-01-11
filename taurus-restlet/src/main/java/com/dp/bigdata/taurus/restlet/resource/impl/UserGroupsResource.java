package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.UserGroupMapper;
import com.dp.bigdata.taurus.generated.module.UserGroup;
import com.dp.bigdata.taurus.generated.module.UserGroupExample;
import com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource;
import com.dp.bigdata.taurus.restlet.shared.UserGroupDTO;

/**
 * UserGroupsResource url : http://xxx/api/group
 * 
 * @author damon.zhu
 */
public class UserGroupsResource extends ServerResource implements IUserGroupsResource {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Override
    @Get
    public ArrayList<UserGroupDTO> retrieve() {
        UserGroupExample example = new UserGroupExample();
        example.or();
        List<UserGroup> groups = userGroupMapper.selectByExample(example);
        ArrayList<UserGroupDTO> groupDtos = new ArrayList<UserGroupDTO>();
        for (UserGroup group : groups) {
            groupDtos.add(new UserGroupDTO(group.getId(), group.getGroupname()));
        }
        return groupDtos;
    }

}
