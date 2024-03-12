package com.jingu.biz.core;

import java.util.List;

public interface IScheduleDataManager {


    /**
     * 获取zookeeper nodes (排序后)
     * @return List<String>
     * @throws Exception e
     */
    List<String> loadScheduleServerNames() throws Exception;

    /**
     * 判断 leader
     * @param uuid 机器uuid
     * @param serverList 需判断服务列表
     * @return boolean
     */
    boolean isLeader(String uuid,List<String> serverList);
}
