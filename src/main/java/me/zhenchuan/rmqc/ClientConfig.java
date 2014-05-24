package me.zhenchuan.rmqc;

import org.skife.config.Config;
import org.skife.config.Default;

public interface ClientConfig {

	@Config("client.type")
	@Default("sync")
	public String clientType();
	
	@Config("async.queue.type")
	@Default("memory")
	public String getAsyncQueueType() ;

	@Config("async.memory.queue.capacity")
	@Default("100000")
	public int getAsyncMemoryQueueCapacity() ;

	@Config("async.filequeue.path")
	public String getAsyncFileQueuePath() ;

	@Config("async.filequeue.name")
	public String getAsyncFileQueueName() ;

	@Config("async.filequeue.gcperid")
	@Default("PT1h")
	public String getAsyncFileQueueGCPeriod() ;

	@Config("async.jobqueue.capacity")
	public int getAsyncJobQueueCapacity();
	/****
	 * 异步发送使用的线程数.
	 * @return
	 */
	@Config("async.sender.threads")
	public int getAsyncSenderThreads() ;

	@Config("retry.count")
	@Default("5")
	public int getRetryCount() ;

	@Config("app")
	@Default("defaultApp")
	public String getApp() ;

	@Config("compression")
	@Default("0")
	public int getCompression() ;

	@Config("async.timeout")
	public long getAsyncTimeout();
	
	/****
	 * 批量发送的message的个数...
	 * @return
	 */
	@Config("async.batchsize")
	public int getAsyncBatchSize() ;

	@Config("topic")
	public String getTopic();
	
	@Config("message.per.second")
	@Default("10000000000")
	public int msgPerSec();
	
	

}
