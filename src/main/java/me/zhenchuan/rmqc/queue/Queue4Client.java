package me.zhenchuan.rmqc.queue;


import me.zhenchuan.rmqc.ClientConfig;
import me.zhenchuan.rmqc.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 根据配置来决定来wrapper哪一种Queue
 */
public class Queue4Client {
    private static final Logger logger = LoggerFactory.getLogger(Queue4Client.class);

    private MessageQueue4Sink queue;

    
    public Queue4Client(ClientConfig config) {
        if (config.getAsyncQueueType().equals("memory")) {
            queue = new MemoryQueue4Sink(config.getAsyncMemoryQueueCapacity());
        } else {
            try {
                queue = new FileQueue4Sink(
                        config.getAsyncFileQueuePath(),
                        config.getAsyncFileQueueName(),
                        config.getAsyncFileQueueGCPeriod());
            } catch (IOException e) {
                logger.error("Exception on initializing Queue4Client: " + e.getMessage(), e);
            }
        }
    }

    public boolean offer(Message msg) {
        return queue.offer(msg);
    }

    public Message poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(timeout, timeUnit);
    }

    public int drain(int batchSize, List<Message> msgList) {
        return queue.drain(batchSize, msgList);
    }

    public void close() {
        queue.close();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public long size() {
        return queue.size();
    }
}

