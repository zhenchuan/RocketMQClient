package me.zhenchuan.rmqc.message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

/**
 * This class implements an {@link Iterable} reader for {@link TMessageSet} so we can iterate
 * over each message in a {@link TMessageSet}.
 *
 * @author jbae
 */
public class MessageSetReader implements Iterable<Message> {
    private static final Logger log = LoggerFactory.getLogger(MessageSetReader.class);

    private final TMessageSet messageSet;

    public MessageSetReader(TMessageSet messageSet) {
        this.messageSet = messageSet;
    }

    public boolean checkCRC() {
        long crcReceived = messageSet.getCrc();
        long crc = MessageSetBuilder.getCRC(messageSet.getMessages());

        return crcReceived == crc;
    }

    @Override
    public Iterator<Message> iterator() {
        try {
            final ByteArrayDataInput input = ByteStreams.newDataInput(
                    Compression.create(messageSet.getCompression()).decompress(messageSet.getMessages()));

            return new Iterator<Message>() {
                private int messageCount = messageSet.getNumMessages();

                @Override
                public boolean hasNext() {
                    return messageCount > 0;
                }

                @Override
                public Message next() {
                    Message m = new Message();
                    try {
                        m.readFields(input);
                        --messageCount;
                        return m;
                    } catch (IOException e) {
                        log.error("Exception while iterating MessageSet:" + e.getMessage(), e);
                        return null;
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove is not supported");
                }
            };
        } catch (Exception e) {
            log.error("Exception while reading: " + e.getMessage(), e);
            return new Iterator<Message>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Message next() {
                    return null;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove is not supported");
                }
            };
        }
    }
}