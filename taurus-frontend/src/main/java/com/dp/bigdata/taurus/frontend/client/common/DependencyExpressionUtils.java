package com.dp.bigdata.taurus.frontend.client.common;

import com.google.gwt.regexp.shared.RegExp;

public final class DependencyExpressionUtils {
	
	   public static final String SPLIT_PART = "(\\[\\w+\\]\\[\\d+\\]\\[\\d+\\])";
	   public static final String DEPENDENCY_PATTERN = SPLIT_PART + "("
	         + "([&|\\|])" + SPLIT_PART + ")*";
	   
	   public static final RegExp regExp = RegExp.compile(DEPENDENCY_PATTERN);
	   
	   public static boolean isLegalDependencyExp(String expressionStr){
		   boolean result = false;		  
		   if (expressionStr != null && !"".equals(expressionStr) && regExp.exec(expressionStr) != null){
			   result = true;
		   }
		   
		   return result;
	   }

}
