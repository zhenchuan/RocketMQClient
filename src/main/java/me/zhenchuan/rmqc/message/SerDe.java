package me.zhenchuan.rmqc.message;

public interface SerDe<T> {
    T deserialize(byte[] payload);

    byte[] serialize(T payload);

    String toString(byte[] payload);
}
