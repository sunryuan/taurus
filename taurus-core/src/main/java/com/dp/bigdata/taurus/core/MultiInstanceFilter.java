package com.dp.bigdata.taurus.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * MultiInstanceFilter
 * 
 * @author damon.zhu
 * 
 */
public class MultiInstanceFilter implements Filter {

	private Filter next;

	private Scheduler scheduler;

	@Autowired
	public MultiInstanceFilter(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<AttemptContext> filter(List<AttemptContext> contexts) {
		HashMap<String, AttemptContext> maps = new HashMap<String, AttemptContext>();

		for (AttemptContext context : contexts) {
			List<AttemptContext> runnings = scheduler.getRunningAttemptsByTaskID(context.getTaskid());
			AttemptContext ctx = maps.get(context.getTaskid());

			if (runnings != null && runnings.size() > 0) {
				// do nothing
			} else {
				if(ctx == null){
					maps.put(context.getTaskid(), context);
				}
			}
		}

		List<AttemptContext> results = new ArrayList<AttemptContext>();
		for (AttemptContext context : maps.values()) {
			results.add(context);
		}

		if (next != null) {
			return next.filter(results);
		} else {
			return results;
		}
	}

	public Filter getNext() {
		return next;
	}

	public void setNext(Filter next) {
		this.next = next;
	}

}