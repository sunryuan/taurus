package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.dp.bigdata.taurus.core.AttemptStatus;
import com.dp.bigdata.taurus.restlet.resource.IAttemptStatusResource;
import com.dp.bigdata.taurus.restlet.shared.StatusDTO;

/**
 * AttemptStatusResource url : http://xxx/api/status
 * 
 * @author damon.zhu
 */
public class AttemptStatusResource extends ServerResource implements IAttemptStatusResource {

    @Override
    @Get
    public ArrayList<StatusDTO> retrieve() {
        ArrayList<StatusDTO> status = new ArrayList<StatusDTO>();
        status.add(new StatusDTO(1, "成功", AttemptStatus.getInstanceRunState(AttemptStatus.SUCCEEDED)));
        status.add(new StatusDTO(1, "杀死", AttemptStatus.getInstanceRunState(AttemptStatus.KILLED)));
        status.add(new StatusDTO(1, "超时", AttemptStatus.getInstanceRunState(AttemptStatus.TIMEOUT)));
        status.add(new StatusDTO(1, "失败", AttemptStatus.getInstanceRunState(AttemptStatus.FAILED)));
        return status;
    }

}
