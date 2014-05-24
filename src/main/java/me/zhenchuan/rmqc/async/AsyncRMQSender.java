package me.zhenchuan.rmqc.async;

import me.zhenchuan.rmqc.AsyncRMQClient;
import me.zhenchuan.rmqc.ClientConfig;
import me.zhenchuan.rmqc.message.MessageSetReader;
import me.zhenchuan.rmqc.message.TMessageSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
/****
 * 发送MessageSet
 * @author crnsnl
 *
 */
public class AsyncRMQSender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AsyncRMQSender.class);

    private final AsyncRMQClient client;
    private final ClientConfig config;
    private final TMessageSet messageSet;
    private final DefaultMQProducer producer;

    public AsyncRMQSender(
            TMessageSet tMessageSet,
            AsyncRMQClient client,ClientConfig config) throws MQClientException {
        this.client = client;
        this.config = config;
        this.messageSet = tMessageSet;
        this.producer = new DefaultMQProducer(config.app());
        this.producer.start();
    }

    public void run() {
        boolean sent = false;
        boolean retried = false;
        long startTS = System.currentTimeMillis();
        Message message = messageSet.toRocketMQMessage(config.topic());
        for (int i = 0; i < config.retryCount(); ++i) {
            try {
            	SendResult sendResult = producer.send(message);
            	if(sendResult.getSendStatus() == SendStatus.SEND_OK){
            		sent = true;
                    retried = i > 0;
                    break;
            	}
            } catch (Exception e) {
                log.error("Exception in send: " + e.getMessage(), e);
                client.updateSenderException();
            }
        }

        if (sent){
            client.updateSendTime(System.currentTimeMillis() - startTS);
            client.updateSentDataStats(messageSet, retried);
        } else {
            for (me.zhenchuan.rmqc.message.Message m : new MessageSetReader(messageSet)) {
                client.restore(m);
            }
        }
    }

    public TMessageSet getMessageSet() {
        return messageSet;
    }
}
