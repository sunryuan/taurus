package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;

/**
 * 
 * IAttemptsResource
 * @author damon.zhu
 *
 */
public interface IAttemptsResource {

   @Get
   public ArrayList<AttemptDTO> retrieve();
}
