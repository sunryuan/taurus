package com.dp.bigdata.taurus.agent.common;

/**
 * TaskType
 * 
 * @author damon.zhu
 */
public enum TaskType {

    SPRING,
    WORMHOLE,
    HADOOP,
    HIVE,
    OTHER;

    public static String getString(TaskType type) {
        switch (type) {
            case SPRING:
                return "spring";
            case WORMHOLE:
                return "wormhole";
            case HADOOP:
                return "hadoop";
            case HIVE:
                return "hive";
            default:
                return "other";
        }
    }

}
