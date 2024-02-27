package com.jingu.biz;

import com.jingu.biz.config.TtmConfig;
import com.jingu.biz.core.IScheduleDataManager;
import com.jingu.biz.core.ScheduleServer;
import com.jingu.biz.zk.ZKManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
@EnableConfigurationProperties(TtmConfig.class)
public class ZkScheduleManager extends ThreadPoolTaskScheduler implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ZkScheduleManager.applicationContext = applicationContext;
    }

    @Resource
    private TtmConfig ttmConfig;

    @PostConstruct
    public void init(){
        this.init(ttmConfig);
    }

    private InitialThread initialThread;
    private IScheduleDataManager scheduleDataManager;
    public void init(TtmConfig ttmConfig){
        if (this.initialThread != null){
            this.initialThread.stopThread();
        }
        this.initLock.lock();
        try {
            this.scheduleDataManager = null;
            if (this.zkManager != null){
                this.zkManager.close();
            }
            this.zkManager = new ZKManager(ttmConfig);
            this.errorMessage = "zk connecting ..."
                    + this.zkManager.getConnectStr();
            initialThread.setName("ScheduleManager-initialThread");
            initialThread.start();
        }catch (Exception e){
           // todo
        } {
            this.initLock.unlock();
        }
    }

    /**
     * 当前调度服务的信息
     */
    protected ScheduleServer currentScheduleServer;

    public ZkScheduleManager() {
        this.currentScheduleServer = ScheduleServer.createScheduleServer(null);
        this.setPoolSize(100);
    }


    private final Lock initLock = new ReentrantLock();
    protected ZKManager zkManager;
    private volatile String errorMessage = "No config Zookeeper connect information";
    class InitialThread extends Thread{
        private transient Logger log = LoggerFactory.getLogger(InitialThread.class);

        ZkScheduleManager sm;

        public InitialThread(ZkScheduleManager sm) {
            this.sm = sm;
        }

        boolean isStop = false;

        public void stopThread() {
            this.isStop = true;
        }

        @Override
        public void run() {
            sm.initLock.lock();
            try {
                int count = 0;
                while (!sm.zkManager.checkZookeeperStatus()){
                    count++;
                    if (count % 50 == 0){
                        sm.errorMessage = "zookeeper connect ..."
                                + sm.zkManager.getConnectStr()
                                + count * 20 + "(ms)";
                        log.error(sm.errorMessage);
                    }
                    Thread.sleep(20);
                    if (this.isStop){
                        return;
                    }
                }
            }catch (Throwable e){
                log.error(">>> ");
            }finally {
                sm.initLock.unlock();
            }
        }
    }
}
