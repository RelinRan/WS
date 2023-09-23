package androidx.ws;

public class MessageBody {
    private String text;
    private OnMessageListener onMessageListener;

    private boolean open;
    private OnConnectListener onConnectListener;

    private OnSendListener onSendListener;

    public MessageBody(String text, OnMessageListener listener) {
        this.text = text;
        this.onMessageListener = listener;
    }

    public MessageBody(boolean open,OnConnectListener onConnectListener) {
        this.open = open;
        this.onConnectListener = onConnectListener;
    }

    public MessageBody(String text, OnSendListener onSendListener) {
        this.text = text;
        this.onSendListener = onSendListener;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OnMessageListener getOnMessageListener() {
        return onMessageListener;
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.onMessageListener = listener;
    }

    public OnConnectListener getOnConnectListener() {
        return onConnectListener;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }

    public OnSendListener getOnSendListener() {
        return onSendListener;
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }
}
