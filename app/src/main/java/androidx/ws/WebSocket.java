package androidx.ws;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * WebSocket客户端
 */
public class WebSocket extends WebSocketClient {

    /**
     * 标识
     */
    private final String TAG = WebSocket.class.getSimpleName();
    /**
     * 消息监听
     */
    private OnMessageListener onMessageListener;
    /**
     * 关闭连接监听
     */
    private OnCloseListener onCloseListener;


    public WebSocket(URI serverURI) {
        super(serverURI);
    }

    public WebSocket(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebSocket(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i(TAG, "status = " + serverHandshake.getHttpStatus() + ",message = " + serverHandshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Log.i(TAG, "received = " + message);
        if (onMessageListener != null) {
            onMessageListener.onReceived(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        //客户端链接失败：code = -1,reason = Connection refused,remote = false
        //服务端断开链接：code = 1006,reason = ,remote = true
        Log.i(TAG, "close code = " + code + ",reason = " + reason + ",remote = " + remote);
        if (onCloseListener != null) {
            onCloseListener.onClose(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.i(TAG, "error = " + ex.toString());
    }

    /**
     * 消息监听
     *
     * @param listener
     */
    public void addMessageListener(OnMessageListener listener) {
        this.onMessageListener = listener;
    }

    /**
     * 服务端关闭监听
     *
     * @param listener
     */
    public void addCloseListener(OnCloseListener listener) {
        this.onCloseListener = listener;
    }

}
