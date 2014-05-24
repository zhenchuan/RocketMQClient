RocketMQClient
==============

RocketMQ的异步发送的方式是用异步网络I/O的方式来完成的

kafka的实现方式为本地内存维护一个队列,异步的过程就是把数据放入队列的过程.

如果由于网络的不稳定性,Kafka的方式会造成数据在内存堆积.同时,如果服务端重启等操作也会导致数据丢失.

而RocketMQ的同步会对主线程进行阻塞.异步的话又依赖于broker的处理能力.还是有可能阻塞主线程服务.


对RocketMQ的调用进行封装,加了一层Queue来尽可能减小对主服务的影响.

同时为了解决内存Queue的弊端,这里采用MappedFileQueue来保证写入速度的情况下来保证数据的安全性.

同时支持一次从Queue中取出N个message,进行打包压缩来减少网络消耗.(如果是采用这种方式,则需要消费端来知晓其设置来做对应的调整)

还支持发送Producer时的限流,防止对主服务的负载产生影响.

#Note
本client的目的是即使broker挂掉,依然可以hold住大量的数据.对主服务不产生(或较小)影响

注意升级你的broker的处理能力!!!

#配置

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

#用法

    Properties properties = new Properties();
    //properties.put("client.type", "async");
    //properties.put("async.queue.type", "file");

	RMQClient client = new RMQClient(properties);
	Message message = new Message("routekey","hello world".getBytes());
    client.send(message);
