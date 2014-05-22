package me.zhenchuan.rmqc.queue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.zhenchuan.rmqc.message.Message;


public interface MessageQueue4Sink {
	
    boolean offer(Message msg);
    Message poll(long timeout, TimeUnit timeUnit) throws InterruptedException;
    /****
     * 从queue中获取最多batchSize个message到msgList中.
     * @param batchSize
     * @param msgList
     * @return
     */
    int drain(int batchSize, List<Message> msgList);
    void close();
    boolean isEmpty();
    long size();

}
