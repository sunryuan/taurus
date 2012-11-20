package com.dp.bigdata.taurus.restlet.utils;

import org.restlet.Request;

/**
 * 
 * RequestExtrator is used to extract object T from the request form.
 * @author damon.zhu
 *
 * @param <T>
 */
public interface RequestExtrator<T> {

    /**
     * 
     * @param request
     * @param isUpdateAction
     * @return
     * @throws Exception
     */
    public T extractTask(Request request, boolean isUpdateAction) throws Exception;
}
