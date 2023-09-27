package androidx.ws;

public interface OnMessageListener {

    /**
     * 指令消息接收
     * @param data 指令消息
     */
    void onReceived(byte[] data);

}
