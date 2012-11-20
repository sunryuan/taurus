package com.dp.bigdata.taurus.core;

import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.Task;

/**
 * 
 * @author damon.zhu
 *
 */
public interface TaskAssignPolicy {
   
   /**
    * assign template a Host to execute it.
    * @param template
    * @return Host which will executes the template
    */
	public Host assignTask(Task task);
}
