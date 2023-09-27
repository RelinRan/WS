package androidx.ws.tunnel;

import androidx.ws.WS;

import java.nio.charset.StandardCharsets;

/**
 * 安全隧道
 */
public class Tunnel extends WS {

    private static Tunnel tunnel;

    /**
     * 隧道客户端
     *
     * @return
     */
    public static Tunnel client() {
        if (tunnel == null) {
            synchronized (WS.class) {
                if (tunnel == null) {
                    tunnel = new Tunnel();
                }
            }
        }
        return tunnel;
    }

    public Tunnel() {

    }

    /**
     * 创建会话
     */
    public void sessionCreate() {
        TunnelHeader header = new TunnelHeader();
        header.setFrame_type(TunnelFrameType.SESSION_CREATE.getType());
        header.setFrame_id(System.currentTimeMillis());
        header.setService_type("ECHO");
        send(new TunnelFrame(header).toBytes());
    }

    /**
     * 响应数据
     *
     * @param code 响应结果码，取值范围0~255，0~15为系统预留响应码，16~255可由您自定义。
     *             0：表示创建Session成功，其他表示失败。
     *             1：表示物联网平台的云端识别到单个安全隧道中Session数量已达到上限（10个），无法再创建。
     *             2：表示设备端拒绝创建该Session。
     * @param msg  创建失败后，返回的错误提示。
     */
    public void commonResponse(int code, String msg) {
        TunnelHeader header = new TunnelHeader();
        header.setFrame_type(TunnelFrameType.COMMON_RESPONSE.getType());
        header.setFrame_id(System.currentTimeMillis());
        header.setService_type("ECHO");
        send(new TunnelFrame(header, new TunnelPayload(code, msg).toBytes()).toBytes());
    }

    /**
     * 发送数据
     *
     * @param payload 数据
     */
    public void dataTransport(String sessionId, String payload) {
        TunnelHeader header = new TunnelHeader();
        header.setFrame_type(TunnelFrameType.DATA_TRANSPORT.getType());
        header.setFrame_id(System.currentTimeMillis());
        header.setSession_id(sessionId);
        header.setService_type("ECHO");
        send(new TunnelFrame(header, payload == null ? new byte[]{} : payload.getBytes(StandardCharsets.UTF_8)).toBytes());
    }

    /**
     * 关闭Session
     *
     * @param code 关闭Session的原因，可取值：
     *             0：表示访问端主动关闭Session。
     *             1：表示设备端主动关闭Session。
     *             2：表示物联网平台因检测到访问端断连，关闭Session。
     *             3：表示物联网平台因检测到设备端断连，关闭Session。
     *             4：表示物联网平台因系统更新，关闭Session，设备端和访问端可以延时1秒后重新建连。
     * @param msg  关闭Session的相关信息。
     */
    public void sessionRelease(int code, String msg) {
        TunnelHeader header = new TunnelHeader();
        header.setFrame_type(TunnelFrameType.SESSION_RELEASE.getType());
        header.setFrame_id(System.currentTimeMillis());
        header.setService_type("ECHO");
        send(new TunnelFrame(header, new TunnelPayload(code, msg).toBytes()).toBytes());
    }
    
    @Override
    public void onReceived(byte[] data) {
        super.onReceived(data);
    }

}
