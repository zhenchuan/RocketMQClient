package me.zhenchuan.rmqc.message;

import com.ning.compress.lzf.LZFDecoder;
import com.ning.compress.lzf.LZFEncoder;

import java.io.IOException;

/**
 * Suro message payload compression
 *
 * The method {@link #compress(byte[])} receives byte[] and returns compressed byte[]
 * The method {@link #decompress(byte[])} receives compressed byte[] and returns uncompressed one
 *
 * 0, NO no compression
 * 1, LZF LZF compression
 *
 * @author jbae
 */
public enum Compression {
    NO(0) {
        byte[] compress(byte[] buffer) {
            return buffer;
        }
        byte[] decompress(byte[] buffer) {
            return buffer;
        }
    },
    LZF(1) {
        byte[] compress(byte[] buffer) {
            try {
                return LZFEncoder.encode(buffer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        byte[] decompress(byte[] buffer) {
            try {
                return LZFDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private final int id;
    Compression(int id) { this.id = id; }

    public int getId() { return id; }

    public static Compression create(int id) {
        for (Compression compression : values()) {
            if (id == compression.getId()) {
                return compression;
            }
        }

        throw new IllegalArgumentException("invalid compression id: " + id);
    }

    abstract byte[] compress(byte[] buffer);
    abstract byte[] decompress(byte[] buffer);
}