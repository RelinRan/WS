package androidx.ws;

import androidx.ws.handshake.ServerHandshake;

public class MessageBody {
    private byte[] data;
    private OnMessageListener onMessageListener;
    private boolean open;
    private OnConnectListener onConnectListener;
    private OnSendListener onSendListener;
    private ServerHandshake serverHandshake;
    private OnOpenListener onOpenListener;

    public MessageBody(byte[] data, OnMessageListener listener) {
        this.data = data;
        this.onMessageListener = listener;
    }

    public MessageBody(boolean open,OnConnectListener onConnectListener) {
        this.open = open;
        this.onConnectListener = onConnectListener;
    }

    public MessageBody(byte[] data, OnSendListener onSendListener) {
        this.data = data;
        this.onSendListener = onSendListener;
    }

    public MessageBody(ServerHandshake serverHandshake, OnOpenListener onOpenListener) {
        this.serverHandshake = serverHandshake;
        this.onOpenListener = onOpenListener;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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

    public ServerHandshake getServerHandshake() {
        return serverHandshake;
    }

    public void setServerHandshake(ServerHandshake serverHandshake) {
        this.serverHandshake = serverHandshake;
    }

    public OnOpenListener getOnOpenListener() {
        return onOpenListener;
    }

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }
}
