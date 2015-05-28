package com.apython.python.apython_pyapp;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/*
 * This handler actually communicates with the other handler of the python host app.
 *
 * Created by Sebastian on 28.05.2015.
 */

public class PythonHostCommunicationHandler extends Handler {

    private Messenger self = new Messenger(this);

    public static final int PROTOCOL_VERSION = 0;

    private enum MessageIdentifiers {
        PROTOCOL_VERSION_HANDSHAKE(0);

        private int id;

        MessageIdentifiers(final int _id) {
            id = _id;
        }

        private static Map<Integer, MessageIdentifiers> map = new HashMap<>();

        static {
            for (MessageIdentifiers identifier : MessageIdentifiers.values()) {
                map.put(identifier.id, identifier);
            }
        }

        public static MessageIdentifiers valueOf(int identifier) {
            return map.get(identifier);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d(MainActivity.TAG, "Got Message!");
        switch (MessageIdentifiers.valueOf(msg.what)) {
        case PROTOCOL_VERSION_HANDSHAKE:
            Log.d(MainActivity.TAG, "Received PROTOCOL_VERSION_HANDSHAKE!");
            Log.i(MainActivity.TAG, "Protocol version: " + msg.arg1);
            break;
        default:
            Log.w(MainActivity.TAG, "Got unexpected message identifier " + msg.what);
            break;
        }

    }

    public void initializeCommunication(Messenger service) {
        this.versionsHandshake(service);
    }

    private void sendMessage(Messenger service, Message msg) {
        msg.replyTo = self;
        try {
            service.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
            // TODO: What now?
        }
    }

    public void versionsHandshake(Messenger service) {
        Message msg = Message.obtain(null, MessageIdentifiers.PROTOCOL_VERSION_HANDSHAKE.id);
        msg.arg1 = PROTOCOL_VERSION;
        this.sendMessage(service, msg);
    }


}
