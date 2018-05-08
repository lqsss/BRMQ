package com.cug.liqiushi.zookeeper;

import com.cug.liqiushi.zookeeper.config.ZKConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by lqs on 2018/5/8.
 */
public class ZkClientHolder {
    private ZKConfig zkConfig;
    private static CuratorFramework zkClient;
    private ZkClientHolder() {
        zkConfig = ZKConfig.getInstance();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                zkConfig.getRetryIntervalMs(), zkConfig.getRetryCount());
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkConfig.getZkServers())
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(zkConfig.getConnectionTimeoutMs())
                .sessionTimeoutMs(zkConfig.getSessionTimeoutMs())
                .build();
    }



    public static CuratorFramework getInstance(){
        return new ZkClientHolder().zkClient;
    }

}
