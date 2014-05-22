package me.zhenchuan.rmqc.queue;


import me.zhenchuan.rmqc.message.Message;
import me.zhenchuan.rmqc.message.MessageSerDe;

import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * File based {@link MessageQueue4Sink}, delegating actual logic to {@link FileBlockingQueue}
 * NOTE !!! NotThreadSafe
 * @author jbae
 */
public class FileQueue4Sink implements MessageQueue4Sink {
    private static final Logger log = LoggerFactory.getLogger(FileQueue4Sink.class);

    public static final String TYPE = "file";

    private final FileBlockingQueue<Message> queue;

    public FileQueue4Sink(
            String path,
            String name,
            String gcPeriod) throws IOException {
        queue = new FileBlockingQueue<Message>(
                path,
                name,
                new Period(gcPeriod == null ? "PT1h" : gcPeriod).toStandardSeconds().getSeconds(),
                new MessageSerDe(),
                true); // auto-commit needed because of message copy
    }

    @Override
    public boolean offer(Message msg) {
        try {
            return queue.offer(msg);
        } catch (Exception e) {
            log.error("Exception on offer: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int drain(int batchSize, List<Message> msgList) {
        return queue.drainTo(msgList, batchSize);
    }

    @Override
    public Message poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    @Override
    public void close() {
        queue.close();
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
