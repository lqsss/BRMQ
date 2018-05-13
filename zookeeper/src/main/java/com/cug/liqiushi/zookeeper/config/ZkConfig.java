package com.cug.liqiushi.zookeeper.config;


import com.cug.liqiushi.zookeeper.util.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lqs on 2018/5/7.
 */
public class ZkConfig {
    private static final Logger log = LoggerFactory.getLogger(ZkConfig.class);
    //读取资源文件 单例
    private static final PropertiesReader propertiesReader = PropertiesReader.getInstance();
    private String zkServers;
    private int connectionTimeoutMs;
    private int sessionTimeoutMs;
    private int retryCount;
    private int retryIntervalMs;

    private ZkConfig() {
        getConfigByReader();
    }

    private static class singletonHolder {
        private static ZkConfig zkConfig = new ZkConfig();
    }

    public static ZkConfig getInstance() {
        return singletonHolder.zkConfig;
    }

    public void getConfigByReader() {
        zkServers = propertiesReader.getValue("zkServers");
        sessionTimeoutMs = Integer.parseInt(propertiesReader.getValue("sessionTimeoutMs"));
        connectionTimeoutMs = Integer.parseInt(propertiesReader.getValue("connectionTimeoutMs"));
        retryCount = Integer.parseInt(propertiesReader.getValue("retryCount"));
        retryIntervalMs = Integer.parseInt(propertiesReader.getValue("retryIntervalMs"));
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryIntervalMs() {
        return retryIntervalMs;
    }

    public void setRetryIntervalMs(int retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
    }
}
