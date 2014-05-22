package me.zhenchuan.rmqc.message;

public interface MessageListener {
	
	public void onLostMessage(Message message);

}
