package me.zhenchuan.rmqc;

import org.skife.config.Config;
import org.skife.config.Default;

public interface ClientConfig {

	@Config("client.type")
	@Default("sync") //支持sync和async 
	public String clientType();
	
	@Config("async.queue.type")
	@Default("memory")//支持file和memory
	public String asyncQueueType() ;

	@Config("async.memory.queue.capacity")
	@Default("100000")//内存队列ArrayBlockingQueue的容量
	public int asyncMemoryQueueCapacity() ;

	@Config("async.filequeue.path")
	@Default("/tmp/mqlocalfilequeue")//mapedfilequeue的目录
	public String asyncFileQueuePath() ;

	@Config("async.filequeue.name")
	@Default("localqueue")//mapedfilequeue的名称
	public String asyncFileQueueName() ;

	@Config("async.filequeue.gcperid")
	@Default("PT1h")//删除已经处理的数据,周期,默认为1小时.格式为joda
	public String asyncFileQueueGCPeriod() ;

	@Config("async.jobqueue.capacity")
	@Default("10")//缓冲的task(每次发送被封装为一个task)的个数.
	public int asyncJobQueueCapacity();
	
	@Config("async.sender.threads")
	@Default("4")//异步发送到broker使用的线程数.线程池.
	public int asyncSenderThreads() ;

	@Config("retry.count")
	@Default("5")//发送broker时的重试次数.
	public int retryCount() ;

	@Config("app")
	@Default("")
	public String app() ;

	@Config("compression")
	@Default("0")//0表示不压缩,1表示使用lzf压缩
	public int getCompression() ;

	@Config("async.timeout")
	@Default("60000")//从queue中获得message的超时时间
	public long getAsyncTimeout();
	
	@Config("async.batchsize")
	@Default("1")//批量发送的message的个数...
	public int asyncBatchSize() ;

	@Config("topic")
	@Default("")
	public String topic();
	
	@Config("message.per.second")
	@Default("100000000")//限制向broker发送的messge的qps
	public int msgPerSec();
	
	

}
