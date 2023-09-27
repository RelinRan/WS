package androidx.ws;

import androidx.ws.client.WebSocketClient;
import androidx.ws.drafts.Draft;
import androidx.ws.handshake.ServerHandshake;
import androidx.ws.util.Print;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * WebSocket客户端
 */
public class WSClient extends WebSocketClient {

    /**
     * 标识
     */
    private final String TAG = WSClient.class.getSimpleName();
    /**
     * 消息监听
     */
    private OnMessageListener onMessageListener;
    /**
     * 关闭连接监听
     */
    private OnCloseListener onCloseListener;
    /**
     * 打开监听
     */
    private OnOpenListener onOpenListener;


    public WSClient(URI serverURI) {
        super(serverURI);
    }

    public WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Print.i(TAG, "status = " + serverHandshake.getHttpStatus() + ",message = " + serverHandshake.getHttpStatusMessage());
        if (onOpenListener != null) {
            onOpenListener.onOpen(serverHandshake);
        }
    }

    @Override
    public void onMessage(String message) {
        Print.i(TAG, "received = " + message);
        if (onMessageListener != null) {
            onMessageListener.onReceived(message.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
        byte[] data = bytes.array();
        String content = new String(data);
        Print.i(TAG, "received = " + content);
        if (onMessageListener != null) {
            onMessageListener.onReceived(data);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        //客户端链接失败：code = -1,reason = Connection refused,remote = false
        //服务端断开链接：code = 1006,reason = ,remote = true
        Print.i(TAG, "close code = " + code + ",reason = " + reason + ",remote = " + remote);
        if (onCloseListener != null) {
            onCloseListener.onClose(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        Print.i(TAG, "error = " + ex.toString());
    }

    /**
     * 添加打开监听
     *
     * @param onOpenListener
     */
    public void addOpenListener(OnOpenListener onOpenListener) {
        this.onOpenListener = onOpenListener;
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
