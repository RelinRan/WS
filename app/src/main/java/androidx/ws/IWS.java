package androidx.ws;

import androidx.ws.drafts.Draft;

import java.util.Map;

/**
 * WebSocket类型客户端
 */
public interface IWS {

    /**
     * 链接服务端
     *
     * @param url 地址
     * @return
     */
    IWS connect(String url);


    /**
     * 链接服务端
     *
     * @param url         地址
     * @param headers     http头部
     * @return
     */
    IWS connect(String url,Map<String, String> headers);

    /**
     * 链接服务端
     *
     * @param url           地址
     * @param protocolDraft 协议草案
     * @return
     */
    IWS connect(String url, Draft protocolDraft);

    /**
     * 链接服务端
     *
     * @param url            地址
     * @param protocolDraft  协议草案
     * @param headers        http头部
     * @param connectTimeout 连接超时
     * @return
     */
    IWS connect(String url, Draft protocolDraft,Map<String, String> headers, int connectTimeout);

    /**
     * 重连
     *
     * @return
     */
    IWS reconnect();

    /**
     * 添加打开监听
     *
     * @param listener 连接监听
     * @return
     */
    long addOpenListener(OnOpenListener listener);

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
     *
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
     * 销毁对象
     */
    void destroy();

    /**
     * 发送数据
     *
     * @param text 内容
     */
    void send(String text);

    /**
     * 发送数据
     *
     * @param data 内容
     */
    void send(byte[] data);

}
