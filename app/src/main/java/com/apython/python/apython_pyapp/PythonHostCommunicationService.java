package com.apython.python.apython_pyapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * This is the service the python host app communicates with.
 *
 * Created by Sebastian on 31.05.2015.
 */

public class PythonHostCommunicationService extends Service {

    // The manager that manages our communication with the Python host.
    final PythonHostConnectionManager communicationManager = new PythonHostConnectionManager();

    @Override
    public IBinder onBind(Intent intent) {
        return communicationManager.getMessengerBinder();
    }
}
