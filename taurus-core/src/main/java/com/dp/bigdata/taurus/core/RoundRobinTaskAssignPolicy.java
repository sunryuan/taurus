package com.dp.bigdata.taurus.core;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.mapper.PoolMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.generated.module.Pool;
import com.dp.bigdata.taurus.generated.module.PoolExample;
import com.dp.bigdata.taurus.generated.module.Task;

/**
 * Task Assign Policy : Round Robin to find the machine to run task.
 * 
 * @author damon.zhu
 */
public class RoundRobinTaskAssignPolicy implements TaskAssignPolicy {

    @Autowired
    private HostMapper hostMapper;
    @Autowired
    private PoolMapper poolMapper;

    private HashMap<Integer, List<Host>> poolNameToHosts; // HashMap<poolID,
                                                          // hosts>
    private HashMap<Integer, Integer> poolCounter; // HashMap<poolID,
                                                   // index>

    public void init() {
        poolNameToHosts = new HashMap<Integer, List<Host>>();
        poolCounter = new HashMap<Integer, Integer>();
        PoolExample example = new PoolExample();
        example.or();
        List<Pool> pools = poolMapper.selectByExample(example);
        for (Pool pool : pools) {
            HostExample hexample = new HostExample();
            hexample.or().andPoolidEqualTo(pool.getId());
            List<Host> hosts = hostMapper.selectByExample(hexample);
            poolNameToHosts.put(pool.getId(), hosts);
        }
    }

    public Host assignTask(Task task) {
        int poolID = task.getPoolid();
        List<Host> hosts = poolNameToHosts.get(poolID);
        int index = getNextHost(poolID);
        return hosts.get(index);
    }

    private int getNextHost(int poolID) {
        Integer counter = poolCounter.get(poolID);
        if (counter == null) {
            counter = 0;
            poolCounter.put(poolID, counter);
        }
        List<Host> hosts = poolNameToHosts.get(poolID);
        int size = hosts.size();
        return (size + counter++) % size;

    }
}
