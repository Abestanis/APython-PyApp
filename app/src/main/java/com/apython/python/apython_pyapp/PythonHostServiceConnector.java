package com.apython.python.apython_pyapp;

/*
 * This connector is responsible for connecting to the python host service.
 *
 * Created by Sebastian on 27.05.2015.
 */

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class PythonHostServiceConnector implements ServiceConnection {

    private MainActivity client;

    public PythonHostServiceConnector(MainActivity client) {
        this.client = client;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.client.setPythonAppConnection(new Messenger(service));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.client.disconnectPythonAppConnection();
    }
}
