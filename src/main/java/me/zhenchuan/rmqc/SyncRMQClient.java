package me.zhenchuan.rmqc;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;

import me.zhenchuan.rmqc.message.Compression;
import me.zhenchuan.rmqc.message.Message;

public class SyncRMQClient implements IRMQClient{
	
    private static final Logger log = LoggerFactory.getLogger(SyncRMQClient.class);
	
    private final ClientConfig config;
    @Deprecated  
    private final Compression compression;	//因为RocksMQ默认可以设置在消息大于N时启动压缩.这里有点多余.
    private final DefaultMQProducer producer;
    
    private AtomicLong sentMessageCount = new AtomicLong(0);
    private AtomicLong lostMessageCount = new AtomicLong(0);
    private AtomicLong retriedCount = new AtomicLong(0);
    private AtomicLong senderExceptionCount = new AtomicLong(0);
    
    
    
	public SyncRMQClient(ClientConfig config) throws MQClientException{
        this.config = config;
        this.compression = Compression.create(config.getCompression());
        this.producer = new DefaultMQProducer(config.getApp());
        this.producer.start();
	}
	
    @Override
    public void send(Message message) {
        send((com.alibaba.rocketmq.common.message.Message)null);
    }

    public boolean send(com.alibaba.rocketmq.common.message.Message message) {
        if (message == null) {
            return false;
        }
        boolean sent = false;
        boolean retried = false;

        for (int i = 0; i < config.getRetryCount(); ++i) {
            try {
            	SendResult sendResult = producer.send(message);
            	if(sendResult.getSendStatus() == SendStatus.SEND_OK){
            		sent = true;
                    retried = i > 0;
                    break;
            	}
            } catch (Exception e) {
                log.error("Exception in send: " + e.getMessage(), e);
                senderExceptionCount.incrementAndGet();
            }
        }

        if (sent) {
            sentMessageCount.incrementAndGet();
            if (retried) {
                retriedCount.incrementAndGet();
            }

        } else {
            lostMessageCount.incrementAndGet();
        }

        return sent;
    }

	@Override
	public long getSentMessageCount() {
		return sentMessageCount.get();
	}

	@Override
	public long getLostMessageCount() {
		return lostMessageCount.get();
	}

}
