package com.cug.liqiushi.broker.config;

import com.cug.liqiushi.broker.util.PropertiesReader;
import com.github.wenweihu86.raft.proto.RaftMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lqs on 2018/5/7.
 */
public class BrokerConfig {
    private static final Logger log = LoggerFactory.getLogger(BrokerConfig.class);
    //读取资源文件 单例
    private static final PropertiesReader propertiesReader = PropertiesReader.getInstance();

    private RaftMessage.Server localNode; //Raft本机
    private List<RaftMessage.Server> raftNodes; //Raft集群
    private String dataDir; // 数据目录
    private int maxSegmentSize; // 单个segment文件最大大小
    private int expiredLogCheckInterval; // log检查时间间隔
    private int expiredLogDuration; // log过期时长
    // 该server属于哪个分片集群，每个分片是leader/followers的raft集群
    private int shardingId;


    /**
     * 内部静态类 singleton
     */
    private static class ConfigurationHolder {
        private static BrokerConfig brokerConfig = new BrokerConfig();
    }

    /**
     * 获取单例
     * BrokerConfig
     * @return
     */
    public static BrokerConfig getInstance() {
        return ConfigurationHolder.brokerConfig;
    }

    //init
    private BrokerConfig() {
        String ipAndPort = propertiesReader.getValue("localNode"); //127.0.0.1:8501
        String ip = ipAndPort.split(":")[0];
        String port = ipAndPort.split(":")[1];
        String id = ipAndPort.split(":")[2];
        localNode = getNode(ip, port, id);
        raftNodes = getRaftServers();
        getOthers();
    }


    private RaftMessage.Server getNode(String ip, String port, String id) {
        RaftMessage.Server.Builder serverBuilder = RaftMessage.Server.newBuilder();
        RaftMessage.EndPoint.Builder endPointBuilder = RaftMessage.EndPoint.newBuilder();
        endPointBuilder.setHost(ip);
        endPointBuilder.setPort(Integer.parseInt(port));
        serverBuilder.setEndPoint(endPointBuilder.build());
        serverBuilder.setServerId(Integer.parseInt(id));
        return serverBuilder.build();
    }

    private List<RaftMessage.Server> getRaftServers() {
        List<RaftMessage.Server> raftNodes = new ArrayList<>();
        String ipAndPorts = propertiesReader.getValue("raftNodes");
        String[] ipAndPortsArr = ipAndPorts.split("&");
        for (String item : ipAndPortsArr) {
            String[] ipAndPort = item.split(":");
            String ip = ipAndPort[0];
            String port = ipAndPort[1];
            String id = ipAndPort[2];
            raftNodes.add(getNode(ip, port, id));
        }
        return raftNodes;
    }

    private void getOthers() {
        dataDir = propertiesReader.getValue("dataDir");
        maxSegmentSize = Integer.parseInt(propertiesReader.getValue("maxSegmentSize"));
        expiredLogCheckInterval = Integer.parseInt(propertiesReader.getValue("expiredLogCheckInterval"));
        expiredLogDuration = Integer.parseInt(propertiesReader.getValue("expiredLogDuration"));
        shardingId = Integer.parseInt(propertiesReader.getValue("shardingId"));
    }


    //getter setter
    public RaftMessage.Server getLocalNode() {
        return localNode;
    }

    public void setLocalNode(RaftMessage.Server localNode) {
        this.localNode = localNode;
    }

    public List<RaftMessage.Server> getRaftNodes() {
        return raftNodes;
    }

    public void setRaftNodes(List<RaftMessage.Server> raftNodes) {
        this.raftNodes = raftNodes;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getMaxSegmentSize() {
        return maxSegmentSize;
    }

    public void setMaxSegmentSize(int maxSegmentSize) {
        this.maxSegmentSize = maxSegmentSize;
    }

    public int getExpiredLogCheckInterval() {
        return expiredLogCheckInterval;
    }

    public void setExpiredLogCheckInterval(int expiredLogCheckInterval) {
        this.expiredLogCheckInterval = expiredLogCheckInterval;
    }

    public int getExpiredLogDuration() {
        return expiredLogDuration;
    }

    public void setExpiredLogDuration(int expiredLogDuration) {
        this.expiredLogDuration = expiredLogDuration;
    }

    public int getShardingId() {
        return shardingId;
    }

    public void setShardingId(int shardingId) {
        this.shardingId = shardingId;
    }
}
