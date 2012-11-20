package com.dp.bigdata.taurus.core;

import java.util.List;

/**
 * 
 * Filter all the triggled taskattempts.
 * @author damon.zhu
 *
 */
public interface Filter {
    
    /**
     * filter the input contexts
     * @param contexts
     * @return List<AttemptContext>
     */
    public List<AttemptContext> filter(List<AttemptContext> contexts);
}
