package com.jingu.biz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ttm")
public class TtmConfig {
    private String zkHost;
    private String zkPort;
    private String rootPath;
    private int zkSessionTimeout;
    private String userName;
    private String password;
    private boolean autoRegisterTask = true;
    private boolean isCheckParentPath = true;
    private String ipBlacklist;
    //机器编号
    private String serverCode;


    /**
     * 192.168.10.103:2181
     * @return {@link String}
     */
    public String getZkConnectionString(){
        return getZkHost() + ":" + getZkPort();
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public String getZkPort() {
        return zkPort;
    }

    public void setZkPort(String zkPort) {
        this.zkPort = zkPort;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutoRegisterTask() {
        return autoRegisterTask;
    }

    public void setAutoRegisterTask(boolean autoRegisterTask) {
        this.autoRegisterTask = autoRegisterTask;
    }

    public boolean isCheckParentPath() {
        return isCheckParentPath;
    }

    public void setCheckParentPath(boolean checkParentPath) {
        isCheckParentPath = checkParentPath;
    }

    public String getIpBlacklist() {
        return ipBlacklist;
    }

    public void setIpBlacklist(String ipBlacklist) {
        this.ipBlacklist = ipBlacklist;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }
}
