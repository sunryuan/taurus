/**
 * Project: taurus-agent
 * 
 * File Created at 2013-1-9
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
package com.dp.bigdata.taurus.agent.exec;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;


/**
 * @author renyuan.sun
 *
 */
public class TaurusExecutorTest {
    
    @Test
    public void testExecute() throws IOException {
//        Executor exec = new TaurusExecutor();
//        exec.execute("test", System.out, System.err, "C:/1.bat");
        String cmd = "sudo -u hadoop bash -c \"kinit\";  ";
        CommandLine cmd1 = new CommandLine(cmd);
        CommandLine  cmdLine = new CommandLine("bash");
        cmdLine.addArgument("-c");
        cmdLine.addArgument(cmd);
        System.out.println(cmd1);
        System.out.println(cmdLine.toString());
    }
}
