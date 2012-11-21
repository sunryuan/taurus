# Overview
RESTful api has two major responsibilities:
*	support client to upload tasks onto taurus
*	support client to view current task executing status

# REST API  
Currently, taurus supports following REST api:
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