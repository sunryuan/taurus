package com.dp.bigdata.taurus.frontend.client.service;

public abstract class ServiceApi {
	//public static final String HOST = "http://127.0.0.1:8888";
	//public static final String HOST = "http://10.1.77.86:8080";
	//public static final String HOST = "http://localhost:8182";
	
//	public static final String UPLOADSERVICE = HOST + "/api/template/create/fileupload";

	public static final String HOST = "http://127.0.0.1:8888";
	public static final String POOLSERVICE = HOST + "/api/pool/";
	public static final String POOLSSERVICE = HOST + "/api/pool";
	public static final String TASKSERVICE = HOST + "/api/task";
//	public static final String TASKSSERVICE = HOST + "/api/task?task_id=";

	public static final String USERSERVICE = HOST + "/api/user";
	public static final String ATTEMPTSSERVICE = HOST + "/api/attempt?task_id=";	
	public static final String HOSTSERVICE = HOST + "/api/host?pool_id=";	
	public static final String HOSTERVICE = "/api/host/";
	public static final String TASKTYPESERVICE = HOST + "/api/tasktypes";
	public static final String WORKFLOWSERVICE = HOST + "/api/workflow";
	public static final String TEMPLATESERVICE = HOST + "/api/template";

}
