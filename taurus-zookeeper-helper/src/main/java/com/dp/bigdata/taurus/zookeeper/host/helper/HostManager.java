/**
 * Project: taurus-zookeeper-helper
 * 
 * File Created at 2013-5-10
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
package com.dp.bigdata.taurus.zookeeper.host.helper;

import java.util.List;


/**
 * operate taurus agent
 * @author renyuan.sun
 *
 */
public interface HostManager {

	void operate(String op, List<String> ips);
    
}
