package com.cug.liqiushi.broker;

import com.cug.liqiushi.broker.util.PropertiesReader;
import com.github.wenweihu86.raft.proto.RaftMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by lqs on 2018/5/7.
 */
public class BrokerConfig {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerConfig.class);
    //读取资源文件 单例
    private static final PropertiesReader propertiesReader = PropertiesReader.getInstance();

    RaftMessage.Server localNode; //Raft本机
    List<RaftMessage.Server> raftNodes; //Raft集群
    private String dataDir; // 数据目录
    private int maxSegmentSize; // 单个segment文件最大大小
    private int expiredLogCheckInterval; // log检查时间间隔
    private int expiredLogDuration; // log过期时长
    // 该server属于哪个分片集群，每个分片是leader/followers的raft集群
    private int shardingId;
    public BrokerConfig(){

    }


}
