package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.dp.bigdata.taurus.restlet.shared.TaskDTO;

/**
 * 
 * ITasksResource
 * @author damon.zhu
 *
 */
public interface ITasksResource {
	
	@Get
	public ArrayList<TaskDTO> retrieve();
	
	@Post
	public void create(Representation re) ;

}
