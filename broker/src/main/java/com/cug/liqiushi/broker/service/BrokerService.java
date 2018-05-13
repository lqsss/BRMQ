package com.cug.liqiushi.client.service;

import com.cug.liqiushi.client.vo.BrokerMessage;

/**
 * RPC service
 * Created by lqs on 2018/5/2.
 */
public interface BrokerService {
    BrokerMessage.PushMessageResponse pushMessage(BrokerMessage.PushMessageRequest request);

    BrokerMessage.PullMessageResponse pullMessage(BrokerMessage.PullMessageRequest request);
}
