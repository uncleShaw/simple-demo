package com.shaw.simplerocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
/**
 * @author yichen
 * @Description: 消费者。订阅方式
 * @date 2021/7/1317:43
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "shaw-consumer", topic = "TopicTest")
public class ConsumerListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info("收到消息，topic:{}, tag:{}, msgId:{}, body:{}", messageExt.getTopic(), messageExt.getTags(),
                messageExt.getMsgId(), message);
    }

}
