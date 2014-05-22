package me.zhenchuan.rmqc;

import me.zhenchuan.rmqc.message.Message;


public interface IRMQClient {
	
    void send(Message message);

    long getSentMessageCount();

    long getLostMessageCount();

}
