package me.zhenchuan.rmqc.message;

import java.nio.ByteBuffer;

public class TMessageSet {

	public String app; // required
	public int numMessages; // required
	public byte compression; // required
	public long crc; // required
	public ByteBuffer messages; // required

	public TMessageSet() {
	}

	public TMessageSet(String app, int numMessages, byte compression, long crc,
			ByteBuffer messages) {
		this.app = app;
		this.numMessages = numMessages;
		this.compression = compression;
		this.crc = crc;
		this.messages = messages;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public int getNumMessages() {
		return numMessages;
	}

	public void setNumMessages(int numMessages) {
		this.numMessages = numMessages;
	}

	public byte getCompression() {
		return compression;
	}

	public TMessageSet setCompression(byte compression) {
		this.compression = compression;
		return this;
	}

	public long getCrc() {
		return crc;
	}

	public void setCrc(long crc) {
		this.crc = crc;
	}

	public byte[] getMessages() {
		return messages == null ? null : messages.array();
	}

	public TMessageSet setMessages(ByteBuffer messages) {
		this.messages = messages;
		return this;
	}
	
	public TMessageSet setMessages(byte[] messages) {
	    setMessages(messages == null ? (ByteBuffer)null : ByteBuffer.wrap(messages));
	    return this;
	}
	
	public com.alibaba.rocketmq.common.message.Message toRocketMQMessage(String topic){
		return new com.alibaba.rocketmq.common.message.Message(topic,getMessages());
	}

}
