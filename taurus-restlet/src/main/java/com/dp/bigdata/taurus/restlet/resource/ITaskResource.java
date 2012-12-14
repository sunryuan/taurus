package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.dp.bigdata.taurus.restlet.shared.TaskDTO;

/**
 * 
 * ITaskResource
 * @author damon.zhu
 *
 */
public interface ITaskResource {
   
   @Get
    public TaskDTO retrieve();
   
   @Post
   public void update(Representation re);
   
   @Delete
   public void remove();

}
