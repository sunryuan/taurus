package com.dp.bigdata.taurus.restlet.utils;

import org.restlet.Request;

import com.mysql.jdbc.StringUtils;
/**
 * 
 * CookieUtils
 * @author damon.zhu
 *
 */
public class CookieUtils {

	/**
	 * 
	 * @param request
	 * @return name
	 */
	public static String getUser(Request request){
		String name = request.getCookies().getValues("user");
		if(StringUtils.isEmptyOrWhitespaceOnly(name)) return "invalid";
		return name;
	}
}
