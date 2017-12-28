package be.tomcools.tombot.tools.codecs;

import be.tomcools.tombot.tools.JSON;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class JsonCodec<T> implements MessageCodec<T, T> {
    private Class<T> theClass;

    private JsonCodec(Class<T> theClass) {
        this.theClass = theClass;
    }

    public static <Y> MessageCodec<Y, Y> forClass(Class<Y> aClass) {
        return new JsonCodec<Y>(aClass);
    }

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        String jsonToStr = JSON.toJson(t);
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public T decodeFromWire(int i, Buffer buffer) {
        // My custom message starting from this *position* of buffer
        int _pos = i;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos += 4, _pos += length);
        return JSON.fromJson(jsonStr, this.theClass);
    }

    @Override
    public T transform(T t) {
        // If a message is sent *locally* across the event bus.
        // This example sends message just as is
        return t;
    }

    @Override
    public String name() {
        return theClass.getName();
    }

    @Override
    public byte systemCodecID() {
        return 0;
    }
}
