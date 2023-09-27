package androidx.ws.tunnel;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * 隧道-用户数据
 */
public class TunnelPayload {

    /**
     * 响应结果码，取值范围0~255，0~15为系统预留响应码，16~255可由您自定义。
     * 0：表示创建Session成功，其他表示失败。
     * 1：表示物联网平台的云端识别到单个安全隧道中Session数量已达到上限（10个），无法再创建。
     * 2：表示设备端拒绝创建该Session。
     */
    private int code;
    private String msg;

    public TunnelPayload(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public TunnelPayload(String json){
        try {
            JSONObject object = new JSONObject(json);
            setCode(object.optInt("code"));
            setMsg(object.optString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public byte[] toBytes() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"code\"").append(":").append(code).append(",");
        if (msg!=null){
            builder.append("\"msg\"").append(":").append("\"").append(msg).append("\"");
        }
        builder.append("}");
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

}
