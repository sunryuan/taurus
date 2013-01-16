package com.dp.bigdata.taurus.web.servlet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IAttemptsResource;
import com.dp.bigdata.taurus.restlet.resource.ITasksResource;
import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;


/**
 * RestletClientSample
 * 
 * @author damon.zhu
 */
public class RestletClientSample{

    public static void main(String args[]){
//       ClientResource cr = new ClientResource("http://10.1.77.85:8182/api/task");
//        ITasksResource resource = cr.wrap(ITasksResource.class);
//         cr.accept(MediaType.APPLICATION_XML);
//        ArrayList<TaskDTO> tasks = resource.retrieve();
//        
//        SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        for(TaskDTO dto : tasks){
//            System.out.println(formatter.format(dto.getAddtime()));
//        }
//        
//        String url = "http://10.1.77.85:8182/api/attempt?task_id=" + "task_201209241101_0001&pageSize=10"; 
//        ClientResource cr1 = new ClientResource(url);
//        cr1.setRequestEntityBuffering(true);
//        IAttemptsResource resource1 = cr1.wrap(IAttemptsResource.class);
//        cr1.accept(MediaType.APPLICATION_JSON);
//        ArrayList<AttemptDTO> attempts = resource1.retrieve();
//        for(AttemptDTO dto : attempts){
//            System.out.println(dto.getStartTime());
//            System.out.println(dto.getStatus());
//        }
    }
}
