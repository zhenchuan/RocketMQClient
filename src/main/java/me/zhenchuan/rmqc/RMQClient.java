package me.zhenchuan.rmqc;

import me.zhenchuan.rmqc.message.Message;


public class RMQClient implements IRMQClient{
	
	private final IRMQClient client;
	
	public RMQClient(){
		client = null;
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

}
