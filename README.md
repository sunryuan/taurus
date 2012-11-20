# Overview #
Taurus is a scheduling system which will schedule all the tasks submitted in the system.

Engine schedules the tasks based on two policies
(Note: in our implementation, they are called triggles): 

*  crontab : Tasks will be tiggled if they meet the conditions of the crontab expression.
*  dependency : Task will be tiggled if its dependent tasks are all finished.
  In our system, dependent condition is represented in the following pattern: [taskName][recentNumber][value]
For example, the expression "[helloworld][1][2]" means that the task with this expression depends on the most recent
execution(1) of the task "helloworld" and its status code is equals 2.

If the task has both crontab and dependency triggles, we will first check its crontab triggle then dependency triggle.


# Install:
    git clone https://github.com/ainilife/taurus.git

    mvn install

    cd taurus-restlet

    cp target/taurus-restlet.tar.gz ( to somewhere )

    tar -xzvf tarusu-restlet.tar.gz

# Configure:
* change conf/restlet.properties according to your system, 
  
  Normaly, "localpath" and "dp.hdfsclinet.keytab.file" have to change
* ask for a keytab.file, and put it into conf directory

# Run:
    ./script/taurus-start.sh standalone | all
'standalone' mode only starts restlet server, while the 'all' mode starts both engine server and restlet server.

# REST API #
    URL                                           Method
    /api/task                                     GET,POST
    /api/task/{task_id}                           GET,POST
    /api/manualtask/{task_id}                     POST
    /api/attempt?task_id={task_id}                GET
    /api/attempt/{attempt_id}                     DELETE
    /api/host/host/{host_id}                      GET,PUT
    /api/host/host?pool_id={xxx}                  GET
    /api/name?task_name={xxx}                     GET
    /api/pool/{pool_id}                           DELETE
    /api/pool                                     GET,POST
    /api/user                                     POST

