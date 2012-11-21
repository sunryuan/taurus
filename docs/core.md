Overview
========


**Engine schedules the tasks based on two policies**
*	crontab expression pattern: * * * * * *	
*	dependency expression pattern: [taskName][recentNumber][value]   
		For example, the expression "[helloworld][1][2]" means that a task depends on the most recent
	execution(1) of the task "helloworld" with its status code 2.   
		Dependency expression also supports and(&), or(|) syntax.

