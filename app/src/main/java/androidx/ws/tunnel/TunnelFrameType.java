package androidx.ws.tunnel;

/**
 * 隧道帧类型
 */
public enum TunnelFrameType {
    COMMON_RESPONSE(1, "响应数据"),
    SESSION_CREATE(2, "创建Session"),
    SESSION_RELEASE(3, "关闭Session"),
    DATA_TRANSPORT(4, "创建Session内的数据传输");

    private int type;
    private String desc;

    TunnelFrameType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
