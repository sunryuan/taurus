/**
 * Project: taurus-agent
 * 
 * File Created at 2013-5-21
 * $Id$
 * 
 * Copyright 2013 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.agent.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 提供task或attempt级别的锁
 * @author renyuan.sun
 *
 */
public class LockHelper {
    private static Map<String, Lock> idToLockMap = new HashMap<String, Lock>();
    
    public static Lock getLock(String id){
        synchronized(idToLockMap){
            Lock lock = idToLockMap.get(id);
            if(lock == null){
                lock = new ReentrantLock();
                idToLockMap.put(id, lock);
            }
            return lock;
        }
    }

}
