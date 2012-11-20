package com.dp.bigdata.taurus.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * MultiInstanceFilter
 * @author damon.zhu
 *
 */
public class MultiInstanceFilter implements Filter{

    private Filter next;
    private Scheduler scheduler;
    
    @Autowired
    public MultiInstanceFilter(Scheduler scheduler){
        this.scheduler = scheduler;
    }
    
    public List<AttemptContext> filter(List<AttemptContext> contexts) {
        HashMap<String, List<AttemptContext>> maps = new HashMap<String, List<AttemptContext>>();
        for(AttemptContext context : contexts){
            List<AttemptContext> runnings = scheduler.getRunningAttemptsByTaskID(context.getTaskid());
                List<AttemptContext> conts = maps.get(context.getTaskid());
                if(conts == null){
                    conts = new ArrayList<AttemptContext>();
                }
                if(runnings == null && conts.size() < context.getAllowmultiinstances()){
                    conts.add(context);
                }else if(runnings.size() + conts.size() < context.getAllowmultiinstances()){
                    conts.add(context);
                }
                maps.put(context.getTaskid(), conts);
        }
        
        List<AttemptContext> results = new ArrayList<AttemptContext>();
        for(List<AttemptContext> lists : maps.values()){
            for(AttemptContext context : lists){
                results.add(context);
            }
        }
        if(next != null){
            return next.filter(results);
        }else{
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
