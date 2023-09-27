package androidx.ws;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.ws.handshake.ServerHandshake;

public class Conversion extends Handler {

    public Conversion() {
    }

    public Conversion(@Nullable Callback callback) {
        super(callback);
    }

    public Conversion(@NonNull Looper looper) {
        super(looper);
    }

    public Conversion(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
    }

    public void connect(OnConnectListener listener,boolean open) {
        send(0, new MessageBody(open,listener));
    }

    public void send(OnSendListener listener, byte[] data) {
        send(1, new MessageBody(data, listener));
    }

    public void received(OnMessageListener listener, byte[] data) {
        send(2, new MessageBody(data, listener));
    }

    public void open(OnOpenListener listener, ServerHandshake serverHandshake) {
        send(3, new MessageBody(serverHandshake, listener));
    }

    private void send(int what, MessageBody msg) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        sendMessage(message);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        MessageBody body = (MessageBody) msg.obj;
        switch (msg.what) {
            case 0:
                body.getOnConnectListener().onConnect(body.isOpen());
                break;
            case 1:
                body.getOnSendListener().onSend(body.getData());
                break;
            case 2:
                body.getOnMessageListener().onReceived(body.getData());
                break;
            case 3:
                body.getOnOpenListener().onOpen(body.getServerHandshake());
                break;
        }
    }

}
