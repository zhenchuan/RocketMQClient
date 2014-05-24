package me.zhenchuan.rmqc;

import java.util.Properties;

import org.skife.config.ConfigurationObjectFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;

import me.zhenchuan.rmqc.message.Message;


public class RMQClient implements IRMQClient{
	
	private final IRMQClient client;
	private final ClientConfig config;
	
	public RMQClient(Properties properties) throws MQClientException{
		ConfigurationObjectFactory factory = new ConfigurationObjectFactory(properties);
		this.config = factory.build(ClientConfig.class);
		if("async".equalsIgnoreCase(config.clientType())){
			client = new AsyncRMQClient(config);
		}else{
			client = new SyncRMQClient(config);
		}
	}

	@Override
	public void send(Message message) {
		client.send(message);
	}

	@Override
	public long getSentMessageCount() {
		return client.getSentMessageCount();
	}

	@Override
	public long getLostMessageCount() {
		return client.getLostMessageCount();
	}
	
	public ClientConfig getConfig(){
		return config;
	}

}
