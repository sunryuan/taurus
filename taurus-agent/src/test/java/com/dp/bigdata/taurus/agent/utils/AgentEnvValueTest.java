/**
 * Project: taurus-agent
 * 
 * File Created at 2012-12-28
 * $Id$
 * 
 * Copyright 2012 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.agent.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * test AgentEnvValue
 * @author renyuan.sun
 *
 */
public class AgentEnvValueTest {
    
    @Test
    public void testGetValue(){
        assertEquals(AgentEnvValue.getValue(AgentEnvValue.KEY_CHECK_INTERVALS, ""),"30000");
        assertEquals(AgentEnvValue.getValue("key_not_exists", ""),"");
        System.out.println( AgentEnvValue.getConfigs());
        System.out.println(         AgentEnvValue.getVersion());

    }
}
