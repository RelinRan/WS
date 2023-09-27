package androidx.ws.tunnel;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class TunnelFrame {

    private TunnelHeader header;
    private byte[] payload;

    public TunnelFrame() {

    }

    public TunnelFrame(byte[] data) {
        int length = data.length;
        if (length > 2) {
            ByteBuffer buffer = ByteBuffer.wrap(data, 0, 2);
            short headerLength = buffer.getShort();
            String headerData = new String(ByteBuffer.wrap(data, 2, headerLength).array());
            header = new TunnelHeader(headerData);
            if (length > headerLength + 2) {
                payload = ByteBuffer.wrap(data, headerLength + 2, length - headerLength).array();
            }
        }
    }

    public TunnelFrame(TunnelHeader header) {
        this.header = header;
    }

    public TunnelFrame(TunnelHeader header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public TunnelHeader getHeader() {
        return header;
    }

    public void setHeader(TunnelHeader header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] toBytes() {
        if (header == null) {
            return new byte[]{};
        }
        String json = header.toJSON();
        byte[] data = json.getBytes(Charset.forName("UTF-8"));
        short length = (short) data.length;
        ByteBuffer buffer = ByteBuffer.allocate(2 + length);
        buffer.put(ByteBuffer.allocate(2).putShort(length).array());
        buffer.put(data);
        return buffer.array();
    }

}
