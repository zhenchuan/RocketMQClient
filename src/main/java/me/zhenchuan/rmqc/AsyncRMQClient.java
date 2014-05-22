package me.zhenchuan.rmqc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import me.zhenchuan.rmqc.async.AsyncRMQSender;
import me.zhenchuan.rmqc.async.RateLimiter;
import me.zhenchuan.rmqc.message.Message;
import me.zhenchuan.rmqc.message.Compression;
import me.zhenchuan.rmqc.message.MessageSetBuilder;
import me.zhenchuan.rmqc.message.MessageSetReader;
import me.zhenchuan.rmqc.message.TMessageSet;
import me.zhenchuan.rmqc.queue.Queue4Client;

public class AsyncRMQClient implements IRMQClient {

	private static final Logger log = LoggerFactory
			.getLogger(AsyncRMQClient.class);

	private final ClientConfig config;
	private final Queue4Client messageQueue;

	private final BlockingQueue<Runnable> jobQueue;
	private final ThreadPoolExecutor senders;
	private final MessageSetBuilder builder;

	private AtomicLong lostMessages = new AtomicLong(0);
	private AtomicLong sentMessages = new AtomicLong(0);
	private AtomicLong restoredMessages = new AtomicLong(0);
	private AtomicLong retriedCount = new AtomicLong(0);

	private final RateLimiter rateLimiter;

	private ExecutorService poller = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(
					"AsyncRMQClientPoller-%d").build());

	public AsyncRMQClient(ClientConfig config, Queue4Client messageQueue,
			int mesPerSec) {
		this.config = config;
		this.messageQueue = messageQueue;
		this.builder = new MessageSetBuilder(config)
				.withCompression(Compression.create(config.getCompression()));
		//使用builder从messageQueue中批量取出数据,交给senders处理.
		poller.execute(createPoller());

		this.jobQueue = new ArrayBlockingQueue<Runnable>(
				config.getAsyncJobQueueCapacity());

		this.rateLimiter = new RateLimiter(mesPerSec);

		this.senders = new ThreadPoolExecutor(config.getAsyncSenderThreads(),
				config.getAsyncSenderThreads(), 10, TimeUnit.SECONDS, jobQueue,
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						TMessageSet messageSet = ((AsyncRMQSender) r)
								.getMessageSet();
						for (Message m : new MessageSetReader(messageSet)) {
							restore(m);
						}
					}
				});
	}

	private boolean running;

	private long lastBatch;

	private Runnable createPoller() {
		running = true;
		final AsyncRMQClient client = this;

		return new Runnable() {
			@Override
			public void run() {
				while (running || !messageQueue.isEmpty()) {
					try {
						Message msg = messageQueue.poll(
								Math.max(0,
										lastBatch + config.getAsyncTimeout()
												- System.currentTimeMillis()),
								TimeUnit.MILLISECONDS);

						boolean expired = (msg == null);
						if (!expired) {
							builder.withMessage(msg.getRoutingKey(),
									msg.getPayload());
							builder.drainFrom(messageQueue,
									config.getAsyncBatchSize() - builder.size());
						}

						boolean full = (builder.size() >= config
								.getAsyncBatchSize());
						if ((expired || full) && builder.size() > 0) {
							lastBatch = System.currentTimeMillis();
							rateLimiter.pause(builder.size());
							senders.execute(new AsyncRMQSender(builder.build(),
									client, config));
						} else if (builder.size() == 0) {
							Thread.sleep(config.getAsyncTimeout());
						}
					} catch (Exception e) {
						log.error(
								"MessageConsumer poller exception: "
										+ e.getMessage(), e);
					}
				}

				builder.drainFrom(messageQueue, (int) messageQueue.size());
				if (builder.size() > 0) {
					try {
						senders.execute(new AsyncRMQSender(builder.build(), client,
								config));
					} catch (Exception e) {
						log.error(
								"MessageConsumer poller exception: "
										+ e.getMessage(), e);
					}
				}
			}
		};
	}

	@PreDestroy
	public void shutdown() {
		running = false;
		poller.shutdown();
		try {
			poller.awaitTermination(5000 + config.getAsyncTimeout(),
					TimeUnit.MILLISECONDS);
			senders.shutdown();
			senders.awaitTermination(5000 + config.getAsyncTimeout(),
					TimeUnit.MILLISECONDS);
			if (!senders.isTerminated()) {
				log.error("AsyncSuroClient didn't terminate gracefully within 5 seconds");
				senders.shutdownNow();
			}
		} catch (InterruptedException e) {
			// ignore exceptions while shutting down
		}
	}

	@Override
	public void send(Message message) {
		if (!messageQueue.offer(message)) {
			lostMessages.incrementAndGet();
		}
	}

	@Override
	public long getSentMessageCount() {
		return sentMessages.get();
	}

	@Override
	public long getLostMessageCount() {
		return lostMessages.get();
	}

	public void restore(Message message) {
		restoredMessages.incrementAndGet();
		send(message);
	}
	
    private AtomicLong senderExceptionCount = new AtomicLong(0);
    
    public void updateSenderException() {
        senderExceptionCount.incrementAndGet();
    }
    
    private long sendTime;
    public void updateSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
    
    public void updateSentDataStats(TMessageSet messageSet, boolean retried) {
        if (retried) {
            retriedCount.incrementAndGet();
        }
    }

}
