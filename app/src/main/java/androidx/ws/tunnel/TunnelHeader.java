package androidx.ws.tunnel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 隧道-首部
 */
public class TunnelHeader {

    public TunnelHeader() {
    }

    public TunnelHeader(String json) {
        try {
            JSONObject object = new JSONObject(json);
            setFrame_type(object.optInt("frame_type"));
            setSession_id(object.optString("session_id"));
            setFrame_id(object.optLong("frame_id"));
            setService_type(object.optString("service_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隧道帧类型。可取值为：
     * 1：common_response，响应数据。
     * 2：session_create，创建Session。
     * 3：session_release，关闭Session。
     * 4：data_transport，创建Session内的数据传输。
     */
    private int frame_type;
    /**
     * 不同类型隧道帧的会话ID，在当前安全隧道内唯一。
     * 访问端发送创建Session的请求帧时，不需要传入该参数，物联网平台会根据收到的请求帧分配一个会话ID，并发送给设备端。其他类型的隧道帧，访问端和设备端均需要传递会话ID。
     */
    private String session_id;
    /**
     * 访问端或设备端发送通信数据时设置的帧ID，取值范围为0~（263-1）。
     * 建议设备端和访问端均使用递增的帧ID，用于区分每个session_id会话中的通信数据。
     */
    private long frame_id;
    /**
     * Session对应的业务类型，由您自定义。支持英文字母、下划线（_）、短划线（-）和英文句号（.），首字母必须为英文字母，最长不超过16个字符。
     */
    private String service_type;

    public int getFrame_type() {
        return frame_type;
    }

    public void setFrame_type(int frame_type) {
        this.frame_type = frame_type;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public long getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(long frame_id) {
        this.frame_id = frame_id;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"frame_type\"").append(":").append(frame_type).append(",");
        if (session_id!=null){
            builder.append("\"session_id\"").append(":").append("\"").append(session_id).append("\"").append(",");
        }
        builder.append("\"frame_id\"").append(":").append(frame_id);
        if (service_type!=null){
            builder.append(",");
            builder.append("\"service_type\"").append(":").append("\"").append(service_type).append("\"");
        }
        builder.append("}");
        return builder.toString();
    }

}
