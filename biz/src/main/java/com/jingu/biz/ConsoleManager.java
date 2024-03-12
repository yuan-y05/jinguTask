package com.jingu.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * task 任务管理工具
 */
public class ConsoleManager {
    private final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);

    private static ZkScheduleManager scheduleManager;


    public static ZkScheduleManager getScheduleManager(){
        if (scheduleManager == null){
            synchronized (ConsoleManager.class){
                scheduleManager = ZkScheduleManager.getApplicationContext().getBean(ZkScheduleManager.class);
            }
        }
        return scheduleManager;
    }

}
