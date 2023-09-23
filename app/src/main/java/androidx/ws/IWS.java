package androidx.ws;

/**
 * WebSocket类型客户端
 */
public interface IWS {

    /**
     * 链接服务端
     *
     * @param url
     * @return
     */
    IWS connect(String url);

    /**
     * 重连
     *
     * @return
     */
    IWS reconnect();

    /**
     * 添加信息监听
     *
     * @param listener 信息监听
     * @return
     */
    long addMessageListener(OnMessageListener listener);

    /**
     * 添加连接监听
     *
     * @param listener 连接监听
     * @return
     */
    long addConnectListener(OnConnectListener listener);

    /**
     * 发送监听
     * @param listener
     * @return
     */
    long addSendListener(OnSendListener listener);

    /**
     * 通过id移除监听
     *
     * @param ids 监听id
     * @return
     */
    void remove(long... ids);

    /**
     * 清除所有监听
     */
    void clear();

    /**
     * 关闭连接
     */
    void close();

    /**
     * @return 是否已经关闭连接
     */
    boolean isClosed();

    /**
     * @return 是否在连接状态
     */
    boolean isConnecting();

    /**
     * @return 是否正在关闭
     */
    boolean isClosing();

    /**
     * @return 是否打开
     */
    boolean isOpen();

    /**
     * @return 刷新数据关闭
     */
    boolean isFlushAndClose();

    /**
     * 发送数据
     *
     * @param text 内容
     */
    void send(String text);

}
