/**
 * Project: hippo-collector
 * 
 * File Created at 2011-11-3
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.zookeeper.common.utils;


/**
 * TODO Comment of ClassLoaderUtils
 * 
 * @author Leo Liang
 * 
 */
public class ClassLoaderUtils {
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Exception ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassLoaderUtils.class.getClassLoader();
		}
		return cl;
	}
}
