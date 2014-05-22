package me.zhenchuan.rmqc.queue;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import me.zhenchuan.rmqc.message.Message;


/**
 * Memory based {@link MessageQueue4Sink}, delegating actual queueing work to {@link ArrayBlockingQueue}
 * NOTE !!! NotThreadSafe
 * @author jbae
 */
public class MemoryQueue4Sink implements MessageQueue4Sink {
    public static final String TYPE = "memory";

    private final BlockingQueue<Message> queue;

    public MemoryQueue4Sink(int capacity) {
        this.queue = new ArrayBlockingQueue<Message>(capacity);
    }

    @Override
    public boolean offer(Message msg) {
        return queue.offer(msg);
    }

    @Override
    public Message poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(timeout, timeUnit);
    }

    @Override
    public int drain(int batchSize, List<Message> msgList) {
        return queue.drainTo(msgList, batchSize);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public long size() {
        return queue.size();
    }
}
