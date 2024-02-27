package com.jingu.biz.core;

import com.jingu.biz.util.ScheduleUtil;

import java.sql.Timestamp;
import java.util.UUID;

public class ScheduleServer {

    private String uuid;

    private String ownSign;

    /**
     * 机器IP地址
     */
    private String ip;

    /**
     * 机器名称
     */
    private String hostName;

    /**
     * 服务开始时间
     */
    private Timestamp registerTime;
    /**
     * 最后一次心跳通知时间
     */
    private Timestamp heartBeatTime;
    /**
     * 最后一次取数据时间
     */
    private Timestamp lastFetchDataTime;
    /**
     * 处理描述信息，例如读取的任务数量，处理成功的任务数量，处理失败的数量，处理耗时
     * FetchDataCount=4430,FetchDataNum=438570,DealDataSuccess=438570,DealDataFail=0,DealSpendTime=651066
     */
    private String dealInfoDesc;

    private String nextRunStartTime;

    private String nextRunEndTime;
    /**
     * 配置中心的当前时间
     */
    private Timestamp centerServerTime;

    /**
     * 数据版本号
     */
    private long version;

    private boolean isRegister;

    public ScheduleServer() {
    }

    public static ScheduleServer createScheduleServer(String ownSign) {
        long currentTimeMillis = System.currentTimeMillis();
        ScheduleServer result = new ScheduleServer();
        result.ownSign = ownSign;
        result.ip = ScheduleUtil.getLocalIp();
        result.hostName = ScheduleUtil.getLocalHostName();
        result.registerTime = new Timestamp(currentTimeMillis);
        result.heartBeatTime = null;
        result.dealInfoDesc = "调度初始化";
        result.version = 0;
        result.uuid = result.ip + "$" +
                (UUID.randomUUID().toString().replace("-","").toUpperCase());
        return result;
    }


}
