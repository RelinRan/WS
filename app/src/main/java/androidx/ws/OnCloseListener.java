package androidx.ws;

public interface OnCloseListener {

    /**
     * socket关闭监听
     *
     * @param code   错误代码
     * @param reason 文字描述
     * @param remote 是否远程已连接
     */
    void onClose(int code, String reason, boolean remote);

}
