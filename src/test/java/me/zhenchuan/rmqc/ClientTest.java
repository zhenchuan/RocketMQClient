package me.zhenchuan.rmqc;

import java.util.Properties;

import me.zhenchuan.rmqc.message.Message;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;


public class ClientTest {
	
	private static final int bench_num = 100000;
	
	private static final String b1024 = RandomStringUtils.randomAscii(1024);
	private static final String b512 = RandomStringUtils.randomAscii(512);
	private static final String b256 = RandomStringUtils.randomAscii(256);
	
	@Test
	public void testSendWithAsyncFileBrokerOffline() throws Exception{
		Properties properties = new Properties();
		properties.put("client.type", "async");
		properties.put("async.queue.type", "file");
		
		RMQClient client = new RMQClient(properties);
		Message message = new Message("routekey",b1024.getBytes());
		long s = System.currentTimeMillis();
		for(int i = 0 ; i<bench_num ;i++){
			client.send(message);
		}
		System.out.println("qps:" + 1.0 * bench_num/((System.currentTimeMillis() - s)/1000.0));
		Thread.sleep(10 *1000);
		
	}

}
