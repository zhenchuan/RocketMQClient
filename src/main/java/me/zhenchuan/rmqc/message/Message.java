package me.zhenchuan.rmqc.message;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 * Suro message payload contains routing key as String and payload as byte[].
 *
 * @author jbae
 */
public class Message {
    public static final BiMap<Byte, Class<? extends Message>> classMap = HashBiMap.create();
    static {
        classMap.put((byte) 0, Message.class);
    }

    private String routingKey;
    private byte[] payload;

    public Message() {}
    public Message(String routingKey, byte[] payload) {
        this.routingKey = routingKey;
        this.payload = payload;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return String.format("routingKey: %s, payload byte size: %d",
                routingKey,
                payload.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!Arrays.equals(payload, message.payload)) return false;
        return !(routingKey != null ? !routingKey.equals(message.routingKey) : message.routingKey != null);

    }

    @Override
    public int hashCode() {
        int result = routingKey != null ? routingKey.hashCode() : 0;
        result = 31 * result + (payload != null ? Arrays.hashCode(payload) : 0);
        return result;
    }

   
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(routingKey);
        dataOutput.writeInt(payload.length);
        dataOutput.write(payload);
    }

    
    public void readFields(DataInput dataInput) throws IOException {
        routingKey = dataInput.readUTF();
        payload = new byte[dataInput.readInt()];
        dataInput.readFully(payload);
    }
}
