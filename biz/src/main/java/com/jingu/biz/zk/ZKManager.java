package com.jingu.biz.zk;

import com.jingu.biz.config.TtmConfig;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKManager {
    private static final transient Logger log = LoggerFactory.getLogger(ZKManager.class);

    private ZooKeeper zk;
    private List<ACL> acl = new ArrayList<ACL>();
    private TtmConfig ttmConfig;

    public ZKManager(TtmConfig ttmConfig) throws Exception {
        this.ttmConfig = ttmConfig;
        this.connect();
    }

    public void close() throws InterruptedException {
        log.info(">>> zk close connect...");
        this.zk.close();
    }

    private void connect() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        createZookeeper(countDownLatch);
        countDownLatch.await();
    }

    private synchronized void reconnect() throws Exception {
        if (this.zk != null){
            this.zk.close();;
            this.zk = null;
            this.connect();;
        }
    }

    private void createZookeeper(final CountDownLatch countDownLatch) throws Exception {
        zk = new ZooKeeper(ttmConfig.getZkConnectionString(),
                ttmConfig.getZkSessionTimeout(),
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        sessionEvent(countDownLatch, watchedEvent);
                    }
                });
        String authString = ttmConfig.getUserName() + ":" + ttmConfig.getPassword();
        zk.addAuthInfo("digest", authString.getBytes());
        acl.clear();;
        acl.add(new ACL(ZooDefs.Perms.ALL,
                new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
        acl.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    private void sessionEvent(CountDownLatch countDownLatch, WatchedEvent event){
        if (event.getState() == Watcher.Event.KeeperState.SyncConnected){
            log.info(">>> zk connect success...");
            countDownLatch.countDown();
        }else if (event.getState() == Watcher.Event.KeeperState.Expired){
            log.info(">>> zk connect expire...");
            try{
                // do connect again
                this.reconnect();
            }catch (Exception e){
                log.error(">>> zk connect fail:{}", e.getMessage(), e);
            }
        }
        // Disconnected：Zookeeper会自动处理Disconnected状态重连
    }

    public boolean checkZookeeperStatus(){
        return zk!=null && zk.getState() == ZooKeeper.States.CONNECTED;
    }

    public String getConnectStr(){
        return this.ttmConfig.getZkConnectionString();
    }
}
