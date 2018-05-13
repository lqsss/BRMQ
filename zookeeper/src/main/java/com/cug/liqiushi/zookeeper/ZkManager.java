package com.cug.liqiushi.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关于zk操作的api
 * Created by lqs on 2018/5/8.
 */
public class ZkManager {
    private static final Logger LOG = LoggerFactory.getLogger(ZkManager.class);
    public static String zkBasePath = "/brmq";//zk根节点
    private CuratorFramework zkClient;

    public ZkManager() {
        zkClient = ZkClientHolder.getInstance();
        zkClient.start();//启动zk客户端连接zk集群

        // create path
        String brokersPath = zkBasePath + "/brokers";
        createPath(brokersPath, CreateMode.PERSISTENT);
        String topicsPath = zkBasePath + "/topics";
        createPath(topicsPath, CreateMode.PERSISTENT);
    }

    public boolean createPath(String path, CreateMode createMode) {
        boolean success = false;
        try {
            zkClient.create()
                    .creatingParentsIfNeeded() //如果父节点不存在，则在创建节点的同时创建父节点
                    .withMode(createMode)
                    .forPath(path);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * broker注册
     * @param shardId
     * @param ip
     * @param port
     */
    public void registerBroker(int shardId, String ip, int port) {
        String path = zkBasePath + "/brokers/" + shardId + "/" + ip + ":" + port;
        boolean success = createPath(path, CreateMode.EPHEMERAL);
        if (success) {
            LOG.info("register broker sucess, ip={}, port={}", ip, port);
        } else {
            LOG.warn("register broker failed, ip={}, port={}", ip, port);
        }
    }
}
