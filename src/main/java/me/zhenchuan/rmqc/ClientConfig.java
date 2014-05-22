package me.zhenchuan.rmqc;

public class ClientConfig {

	public String getAsyncQueueType() {
		return null;
	}

	public int getAsyncMemoryQueueCapacity() {
		return 0;
	}

	public String getAsyncFileQueuePath() {
		return null;
	}

	public String getAsyncFileQueueName() {
		return null;
	}

	public String getAsyncFileQueueGCPeriod() {
		return null;
	}

	public int getAsyncJobQueueCapacity() {
		return 0;
	}
	/****
	 * 异步发送使用的线程数.
	 * @return
	 */
	public int getAsyncSenderThreads() {
		return 0;
	}

	public int getRetryCount() {
		return 0;
	}

	public String getApp() {
		return null;
	}

	public int getCompression() {
		return 0;
	}

	public long getAsyncTimeout() {
		return 0;
	}
	/****
	 * 批量发送的message的个数...
	 * @return
	 */
	public int getAsyncBatchSize() {
		return 0;
	}

	public String getTopic() {
		return null;
	}
	
	

}
