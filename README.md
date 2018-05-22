# BRMQ

BRMQ的开发记录，其中包括以下几点：
1. 日常进度
2. 问题小结
3. 资料整理
4. 学习总结（在开发过程中收获的知识）

本文档目的出于对BMRQ的一个记录，供自己观看，以时间轴顺序记载，写的比较简洁。
## 5.8之前
### 架构设计：参照一些现在主流mq，
1. 消费群组
2. 根据订阅topic pull/push
3. 对topic进行分区
4. 整个架构通过zookeeper来进行协同

### 想法演变
**问题关键在于** broker（消息代理）上搭载着我们的Raft算法，如图中部broker集群（1L2F,Raft集群通常是3个节点,1领导人，2follwer）,**怎么和我们的队列来进行结合。（图中的apply过程）**
### 最开始的想法
![](http://op7scj9he.bkt.clouddn.com/%E5%9B%BE%E4%B8%80.JPG)
1. 我们用三个节点（broker集群）仅仅只搭载Raft算法，然后用多个节点针对某一个topic进行分区（1个节点对应1个分区），然后Raft集群和消息存储节点之间进行通信，broker节点通过Raft算法保证broker集群有着4个分区的replication？
**不行：**
1. 将消息存储和一致性算法分开了，目前觉得节点多而浪费。
2. 如果是一个broker集群上有所有分区副本，消费者/生产者读写都通过leader，那还要分区干嘛。

### 思考了一下
topic里的消息就应该直接存储在broker上，我上面那个太蠢了，先来4个节点存储消息，再搞3个节点存储副本（每个节点都有4个分区的副本）。分区本来就是为了解决IO瓶颈问题，broker集群上存储着所有消息，每个消费者/生产者对此进行读写，性能大大有问题。

![](https://upload-images.jianshu.io/upload_images/5753761-e04f6a114272f69b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

参考了一下kafka，再加上我们的一致性算法保证副本一致性。
**对图的解释：** 我们还是有3个节点构成broker集群，serverId分别为1、2、3，在节点的0、1、2、3代表的topic下的4个分区消息，图中一半阴影一半空白表示该节点是本分区的leader（和client通信接收指令，以及和集群其他节点通信，保证它们有着相同的分区副本）。

4个分区分散到三个serverId分别为1、2、3的broker集群，每个节点上都有某个分区的leader和其他分区的replication，比如id:2的节点是topic分区2的leader，也是分区0、1、3的follower，有着它们的replication。一个节点可能是多个分区的leader，比如id:1的节点是topic分区0的leader，也是分区1的leader。

**不行**
1. 可能需要改变Raft算法，因为我们的Raft只有一个leader，多个follower，而此处的猜想是每个分区消息对应一个leader，1个节点可能有多个分区leader。**实力不够！**

### 最终敲定
topic下的一个分区消息存储在一个broker集群里（当然也可以多个分区存储在一个集群啦），broker集群里的每个节点有着相同的分区副本。
![](http://op7scj9he.bkt.clouddn.com/IMG_1857.JPG)
如图，我们的producer生产一个topic-0的消息，根据zookeeper上注册的一些节点信息，producer找到了shardId为1的broker集群，接下来就是Raft的功劳啦！！

### ProtoBuf
学习了一下谷歌的ProtoBuf，引用到项目中。
记录的一篇配置文章[ProtoBuf生成](https://lqsss.github.io/2018/05/02/protobuf/)

### 其他问题
1. cg（consumer group）
- 作用
>引入多个consumer的初衷大多是为了提升消费性能，即提升消费的吞吐量。试想你的业务消费代码打算消费100个分区的数据，使用一个consumer消费有很大可能使得各个分区的消费进度不均匀，且单个consumer单次poll回来的数据量是有限制的，最终消费端总的TPS也受限于单consumer的性能。

- 设定
>既然是一个组，那么组内必然可以有多个消费者或消费者实例(consumer instance)，它们共享一个公共的ID，即group ID。组内的所有消费者协调在一起来消费订阅主题(subscribed topics)的所有分区(partition)。当然，每个分区只能由同一个消费组内的一个consumer来消费。

2. partition
 topic是逻辑概念，partition是屋里概念，producer只关心消息发往哪个topic，而consumer只关心自己订阅哪个topic。
如果所有的消息处于一个broker来处理的话，那么这个broker就会成为瓶颈，无法做到水平扩展，将topic的消息分散到整个集群，也就是类似于分布式存储系统里的分片。

3. zookeeper
emmm，暂时不说。

### 参考
[kafka中的topic为什么要进行分区?](https://www.zhihu.com/question/28925721)
[Kafka消费组(consumer group)](https://www.cnblogs.com/huxi2b/p/6223228.html)
[kafka消费者组数量较大对性能有什么影响？](https://www.zhihu.com/question/57278539)

## 5.8
### 进度
1. zookeeper单机伪集群配置
2. Curator控制zk代码编写
 
- 主要配置
```java
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.1.109:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        
        client.close();
    }    
```
## 参考
[zookeeper入门（1）在单机上实现ZooKeeper伪机群/伪集群部署](https://blog.csdn.net/poechant/article/details/6633923)
[ZooKeeper(3.4.5) - 开源客户端 Curator(2.7.0) 的简单示例](http://www.cnblogs.com/huey/p/4307724.html)

### 收获的其他知识
工厂模式和构建者模式的应用场景，好奇java的内部类作用
[factory pattern](https://lqsss.github.io/2018/05/09/%E5%B7%A5%E5%8E%82%E6%A8%A1%E5%BC%8F/)
[builder pattern](https://lqsss.github.io/2018/05/09/%E6%9E%84%E5%BB%BA%E8%80%85%E6%A8%A1%E5%BC%8F/)
[内部类](https://lqsss.github.io/2018/05/09/%E4%B8%BA%E4%BB%80%E4%B9%88%E9%9C%80%E8%A6%81%E5%86%85%E9%83%A8%E7%B1%BB/)


## 5.10
### 进度
1. Broker的大致架构构思
2. ZkManager的编写

- 启动zkclient连接伪集群
- RMBQ里 broker模块里 需要broker信息列表向zk注册，未来消费者生产者需要通过zk上的broker的节点信息进行沟通， /broker/shardId/xxx,xxx,xxx(xxx代表消息代理节点，shardId代表这个broker集群的分片id)，这里节点都是CreateMode.PERSISTENT。
- zk需要保存topic。
/broker/topics/xxx(xxx代表zk服务端保存的主题)

3. broker里的raft服务、状态机等注册。broker服务里有消费者pull和生产者push信息的两种服务，需要将此通过RPC（动态代理）完成

### 下一步
参考一下给出的Raft使用demo，完成

### 收获知识点
1. zk节点类型
- CreateMode.EPHEMERAL 临时节点 跟session生命周期有关
临时节点下面不能创建子节点。
- CreateMode.EPHEMERAL_SEQUENTIAL 临时顺序节点
分布式锁(暂时空缺)
- CreateMode.PERSISTENT 持久节点
- CreateMode.PERSISTENT 持久顺节点
默认的Session超时时间是在2 * tickTime ~ 20 * tickTime(心跳数)

### 参考
[ZooKeeper的“会话终止”是这么出现的](https://blog.csdn.net/jiyiqinlovexx/article/details/42649487)

## 5.11
娱乐学习了一下
[分布式锁实现 - zookeeper](https://lqsss.github.io/2018/05/12/%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81-zookeeper/)
[zookeeper之监听事件总结](https://my.oschina.net/u/1540325/blog/610347)

## 5.12 
### 进度
1. 状态机方面代码研究
    实现Raft提供的接口，主要完成三个方法：
- writeSnapshot
- readSnapshot
- apply

关于Raft快照这方面暂时空缺，主要来看下apply（应用到状态机上），当Raft集群中的leader确认集群中大部分节点都将此消息存入日志中，改变状态机的状态，返回client ack消息。

2. 数据结构的设计（对于存储消息）
按照之前设定的broker架构，每一个raft集群掌管一个queueId，保证一个队列的副本一致性，这里queueId比较抽象。

etc，``topic/queueId/xxx.log``,实际对应硬盘中/某个具体topic/queueId的文件夹,在具体queueId文件夹下存放着(根据一定的决策分区的)对应的消息文件，消息文件里存放着一列一列的消息请求实体(content)

queue对应多个segmentedLog，segmentedLog里有多个segement文件，segement里是消息实体

``ConcurrentHashMap<String, ConcurrentHashMap<Integer, SegmentedLog>>``
key:topic,value:也是一个ConcurrentHashMap，多个queueId对应的SegmentedLog文件夹；

SegmentedLog里有``TreeMap<Long, Segment>``
key:存储起始offset，value:对应的Segment

xx/topic/queueId/start-end.log (文件名信息：存储的第一行消息在整个消息队列的offset，以及最后一行消息的offset)

### 其他
1. zk删除父路径，若底下有子节点 会报异常
2. Curator``creatingParentsIfNeeded()`` 会把前面的所有没有创建的路径全部创建一遍

## 5.13
**今天母亲节！**
### 进度
之前的快照读/写取代码先放一下，当前主要是apply。
LogManager管理``ConcurrentMap<String, ConcurrentMap<Integer, SegmentedLog>>``这一数据结构
节点启用时，读取快照，此时会进行读取messages文件夹下的文件
1. 遍历topic文件夹，对于每一个topic创建Map<Integer, SegmentedLog> queueMap
2. 嵌套遍历，遍历1得到的当前topic底下的分区，目的：得到SegmentedLog填充queueMap
3. 嵌套遍历，遍历2得到的当前分区下的segement文件，目的：新建Segement对象(根据文件名，得到在队列的offset)，填充TreeMap<Long, Segment> startOffsetSegmentMap
Raft启动时，LogManager在构造时,读取文件信息，完成这些数据结构的初始化。

