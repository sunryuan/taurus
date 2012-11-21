Overview Architecture
========
![png](https://raw.github.com/ainilife/Taurus-1/master/docs/img/taurus.png)

Scheduling policies
--------------------
*	crontab expression pattern: * * * * * *	

*	dependency expression 
	*	pattern: [taskName][recentNumber][value]   
	*	and(&), or(|) syntax supported
	*	For example, the expression "[helloworld][1][2]" means that a task depends on the most recent
	execution(1) of the task "helloworld" with its status code 2.   

