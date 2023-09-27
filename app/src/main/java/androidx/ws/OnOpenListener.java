package androidx.ws;

import androidx.ws.handshake.ServerHandshake;

public interface OnOpenListener {

    void onOpen(ServerHandshake serverHandshake);

}
