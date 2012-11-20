package com.dp.bigdata.taurus.core;

import com.dp.bigdata.taurus.core.parser.Operation;

/**
 * AttemptStatusCheck
 * 
 * @author damon.zhu
 * @see Operation
 */
public interface AttemptStatusCheck {

    /**
     * Check whether the <code>Operation</code> is finished.
     * 
     * @param operation
     * @return true if the operation finished, false otherwise.
     */
    public boolean isDone(Operation operation);
}
