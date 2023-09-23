package androidx.ws;

public interface OnMessageListener {

    /**
     * 指令消息接收
     * @param message 指令消息
     */
    void onReceived(String message);

}
