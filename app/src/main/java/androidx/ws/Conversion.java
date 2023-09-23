package androidx.ws;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public void send(OnSendListener listener, String text) {
        send(1, new MessageBody(text, listener));
    }

    public void received(OnMessageListener listener, String text) {
        send(2, new MessageBody(text, listener));
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
                body.getOnSendListener().onSend(body.getText());
                break;
            case 2:
                body.getOnMessageListener().onReceived(body.getText());
                break;
        }
    }

}
