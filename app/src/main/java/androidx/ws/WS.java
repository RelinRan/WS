package androidx.ws;

import android.util.Log;

import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket客户端
 */
public class WS implements IWS, OnCloseListener, OnMessageListener {

    private final String TAG = WS.class.getSimpleName();
    /**
     * 重连时间 - 单位毫秒
     */
    private final int RECONNECT_TIME = 3;
    /**
     * 客户端
     */
    private WebSocket client;
    /**
     * 是否已连接
     */
    private boolean isOpen;
    /**
     * 连接地址
     */
    private String url;
    /**
     * 连接监听
     */
    private ConcurrentHashMap<Long, OnConnectListener> connectMap;
    /**
     * 消息监听
     */
    private ConcurrentHashMap<Long, OnMessageListener> messageMap;
    /**
     * 发送监听
     */
    private ConcurrentHashMap<Long, OnSendListener> sendMap;
    /**
     * 线程池
     */
    private ExecutorService executorService;
    /**
     * 线程池操作对象
     */
    private Future future;
    /**
     * 定时
     */
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture scheduledFuture;
    private Conversion conversion;

    private static WS ws;

    public static WS client() {
        if (ws == null) {
            synchronized (WS.class) {
                if (ws == null) {
                    ws = new WS();
                }
            }
        }
        return ws;
    }

    private WS() {
        conversion = new Conversion();
    }

    /**
     * 消息监听
     *
     * @param listener
     */
    @Override
    public long addMessageListener(OnMessageListener listener) {
        long id = System.currentTimeMillis();
        if (messageMap == null) {
            messageMap = new ConcurrentHashMap<>();
        }
        messageMap.put(id, listener);
        return id;
    }

    @Override
    public long addConnectListener(OnConnectListener listener) {
        long id = System.currentTimeMillis();
        if (connectMap == null) {
            connectMap = new ConcurrentHashMap<>();
        }
        connectMap.put(id, listener);
        return id;
    }

    @Override
    public long addSendListener(OnSendListener listener) {
        long id = System.currentTimeMillis();
        if (sendMap == null) {
            sendMap = new ConcurrentHashMap<>();
        }
        sendMap.put(id, listener);
        return id;
    }

    @Override
    public void remove(long... ids) {
        for (long id : ids) {
            if (messageMap != null) {
                messageMap.remove(id);
            }
            if (connectMap != null) {
                connectMap.remove(id);
            }
            if (sendMap != null) {
                sendMap.remove(id);
            }
        }
    }

    @Override
    public void clear() {
        if (messageMap != null) {
            messageMap.clear();
        }
        if (connectMap != null) {
            connectMap.clear();
        }
        if (sendMap != null) {
            sendMap.clear();
        }
    }

    @Override
    public void onReceived(String message) {
        Log.i(TAG,"received "+message);
        if (messageMap != null) {
            for (Long key : messageMap.keySet()) {
                conversion.received(messageMap.get(key), message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isOpen = false;
        reconnect();
    }

    @Override
    public IWS connect(String url) {
        this.url = url;
        if (isOpen()) {
            return this;
        }
        if (client == null) {
            client = new WebSocket(URI.create(url));
            /**{@link #onReceived}**/
            client.addMessageListener(this);
            /**{@link #onClose}**/
            client.addCloseListener(this);
        }
        if (client.isOpen()) {
            Log.i(TAG, "connect isOpen = true and isConnecting = true");
            return ws;
        }
        Log.i(TAG, "server = " + url);
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        if (future != null) {
            future.cancel(true);
        }
        future = executorService.submit(() -> {
            try {
                ReadyState state = client.getReadyState();
                Log.i(TAG, "state = " + state);
                if (!state.equals(ReadyState.OPEN)) {
                    if (state.equals(ReadyState.NOT_YET_CONNECTED)) {
                        isOpen = client.connectBlocking();
                    } else if (state.equals(ReadyState.CLOSING) || state.equals(ReadyState.CLOSED)) {
                        isOpen = client.reconnectBlocking();
                    }
                }
                Log.i(TAG, "connect isOpen = " + isOpen);
                if (connectMap != null) {
                    for (Long key : connectMap.keySet()) {
                        conversion.connect(connectMap.get(key), isOpen);
                    }
                } else {
                    Log.i(TAG, "connect map is null");
                }
                if (!isOpen) {
                    reconnect();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    @Override
    public IWS reconnect() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        }
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = scheduledExecutorService.schedule(() -> {
            if (!client.getReadyState().equals(ReadyState.OPEN)) {
                Log.i(TAG, "reconnect...");
                connect(url);
            } else {
                scheduledFuture.cancel(true);
            }
        }, RECONNECT_TIME, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            isOpen = false;
            client = null;
            Log.i(TAG, "close");
        }
        //取消连接
        if (future != null) {
            future.cancel(true);
        }
        //取消重连定时任务
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    @Override
    public boolean isClosed() {
        return client == null ? false : client.isClosed();
    }

    @Override
    public boolean isConnecting() {
        return client == null ? false : client.isOpen();
    }

    @Override
    public boolean isClosing() {
        return client == null ? false : client.isClosing();
    }

    @Override
    public boolean isOpen() {
        return client == null ? false : client.isOpen();
    }

    @Override
    public boolean isFlushAndClose() {
        return client == null ? false : client.isFlushAndClose();
    }

    @Override
    public void send(String text) {
        if (client == null) {
            return;
        }
        if (client.isClosing()) {
            return;
        }
        if (client.isClosed()) {
            return;
        }
        if (isOpen && client.isOpen()) {
            client.send(text);
            if (sendMap != null) {
                for (Long key : sendMap.keySet()) {
                    conversion.send(sendMap.get(key), text);
                }
            }
            Log.i(TAG, "send " + text);
        }
    }

}
