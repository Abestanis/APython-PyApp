package com.apython.python.apython_pyapp;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/*
 * This handler handles requests from/to the Python host app.
 *
 * Created by Sebastian on 28.05.2015.
 */

public class PythonHostCommunicationHandler extends Handler {

    // A messenger of this class, used to provide an address for the python client app to answer to.
    private Messenger self = new Messenger(this);

    // An interface to implement a callback function to handle an incoming message.
    private ConnectionManager connectionManager;
    public interface ConnectionManager {
        void onMessage(Message message);
    }

    public PythonHostCommunicationHandler(ConnectionManager manager) {
        super();
        this.connectionManager = manager;
    }

    @Override
    public void handleMessage(Message message) {
        this.connectionManager.onMessage(message);
    }

    public IBinder getBinder() {
        return this.self.getBinder();
    }

    private void sendMessage(Messenger messenger, Message msg) {
        msg.replyTo = self;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
            // TODO: What now?
        }
    }
}
