package androidx.ws;

import androidx.ws.drafts.Draft;
import androidx.ws.drafts.Draft_6455;
import androidx.ws.enums.ReadyState;
import androidx.ws.framing.CloseFrame;
import androidx.ws.handshake.ServerHandshake;
import androidx.ws.util.Print;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
public class WS implements IWS, OnOpenListener, OnCloseListener, OnMessageListener {

    private String TAG = WS.class.getSimpleName();
    /**
     * 重连时间 - 单位毫秒
     */
    private final int RECONNECT_TIME = 3;
    /**
     * 客户端
     */
    private WSClient client;
    /**
     * 是否已连接
     */
    private boolean isOpen;
    /**
     * 连接地址
     */
    private String url;
    /**
     * 打开监听
     */
    private ConcurrentHashMap<Long, OnOpenListener> openMap;
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
    private boolean debug;

    /**
     * 清除map
     *
     * @param map
     */
    private void clearMap(Map map) {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * websocket客户端
     *
     * @param single 是否单例模式
     * @return
     */
    public static WS client(boolean single) {
        if (single) {
            if (ws == null) {
                synchronized (WS.class) {
                    if (ws == null) {
                        ws = new WS();
                    }
                }
            }
        } else {
            ws = new WS();
        }
        return ws;
    }

    protected WS() {
        conversion = new Conversion();
    }

    @Override
    public long addOpenListener(OnOpenListener listener) {
        long id = System.currentTimeMillis();
        if (openMap == null) {
            openMap = new ConcurrentHashMap<>();
        }
        openMap.put(id, listener);
        return id;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (client != null) {
            client.setDebug(debug);
        }
    }

    public boolean isDebug() {
        return debug;
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
            if (openMap != null) {
                openMap.remove(id);
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
        if (openMap != null) {
            openMap.clear();
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if (openMap != null) {
            for (Long key : openMap.keySet()) {
                conversion.open(openMap.get(key), serverHandshake);
            }
        }
    }

    @Override
    public void onReceived(byte[] data) {
        if (messageMap != null) {
            for (Long key : messageMap.keySet()) {
                conversion.received(messageMap.get(key), data);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isOpen = false;
        //正常关闭客户端
        if (code == CloseFrame.NORMAL) {
            Print.i(TAG, "normal closure");
        } else {
            reconnect();
        }
    }

    @Override
    public IWS connect(String url) {
        return connect(url, new Draft_6455(), null, 0);
    }

    @Override
    public IWS connect(String url, Map<String, String> headers) {
        return connect(url, new Draft_6455(), headers, 0);
    }

    @Override
    public IWS connect(String url, Draft protocolDraft) {
        return connect(url, protocolDraft, null, 0);
    }

    @Override
    public IWS connect(String url, Draft protocolDraft, Map<String, String> headers, int connectTimeout) {
        this.url = url;
        if (isOpen()) {
            return this;
        }
        if (client == null) {
            client = new WSClient(URI.create(url), protocolDraft, headers, connectTimeout);
            client.setDebug(debug);
            client.addOpenListener(this);/**{@link #onOpen(ServerHandshake)}**/
            client.addMessageListener(this);/**{@link #onReceived(String)}**/
            client.addCloseListener(this); /**{@link #onClose(int, String, boolean)}**/
        }
        if (client.isOpen()) {
            Print.i(TAG, "connect isOpen = true and isConnecting = true");
            return ws;
        }
        Print.i(TAG, "server = " + url);
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        if (future != null) {
            future.cancel(true);
        }
        future = executorService.submit(() -> {
            try {
                ReadyState state = client.getReadyState();
                Print.i(TAG, "state = " + state);
                if (!state.equals(ReadyState.OPEN)) {
                    if (state.equals(ReadyState.NOT_YET_CONNECTED)) {
                        isOpen = client.connectBlocking();
                    } else if (state.equals(ReadyState.CLOSING) || state.equals(ReadyState.CLOSED)) {
                        isOpen = client.reconnectBlocking();
                    }
                }
                Print.i(TAG, "connect isOpen = " + isOpen);
                if (connectMap != null) {
                    for (Long key : connectMap.keySet()) {
                        conversion.connect(connectMap.get(key), isOpen);
                    }
                } else {
                    Print.i(TAG, "connect map is null");
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
                Print.i(TAG, "reconnect...");
                connect(url);
            } else {
                scheduledFuture.cancel(true);
            }
        }, RECONNECT_TIME, TimeUnit.SECONDS);
        return this;
    }


    @Override
    public void close() {
        if (client != null && client.isOpen()) {
            client.close();
            Print.i(TAG, "close");
        }
        isOpen = false;
        client = null;
        //取消连接
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        //取消重连定时任务
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    /**
     * 阻塞关闭
     */
    @Override
    public void closeBlocking() {
        if (client != null && client.isOpen()) {
            try {
                client.closeBlocking();
                Print.i(TAG, "closeBlocking");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isOpen = false;
        client = null;
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
    public boolean isOpen() {
        return client == null ? false : client.isOpen();
    }

    @Override
    public boolean isClosing() {
        return client == null ? false : client.isClosing();
    }

    @Override
    public boolean isFlushAndClose() {
        return client == null ? false : client.isFlushAndClose();
    }

    @Override
    public void send(String text) {
        send(text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void send(byte[] data) {
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
            client.send(data);
            if (sendMap != null) {
                for (Long key : sendMap.keySet()) {
                    conversion.send(sendMap.get(key), data);
                }
            }
        }
    }

    /**
     * 释放对象
     */
    @Override
    public void free() {
        future = null;
        scheduledExecutorService = null;
        scheduledFuture = null;
        url = null;
        clearMap(openMap);
        clearMap(connectMap);
        clearMap(messageMap);
        clearMap(sendMap);
        openMap = null;
        connectMap = null;
        messageMap = null;
        sendMap = null;
        if (conversion != null) {
            conversion.removeCallbacksAndMessages(null);
        }
        conversion = null;
        ws = null;
    }

    @Override
    public void destroy() {
        close();
        free();
    }

}
