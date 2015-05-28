package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Messenger;
import android.util.Log;

/*
 * This manager handles the entire Communication between the python host and us.
 *
 * Created by Sebastian on 28.05.2015.
 */

public class PythonHostConnectionManager {

    public static final String TAG = MainActivity.TAG;

    // A Connector for establishing the connection to the python host service.
    private PythonHostServiceConnection connection;

    private PythonHostCommunicationHandler communicationHandler;

    private static final int PYTHON_HOST_LOOKUP_REQUEST_CODE = 0;

    private Activity mainActivity;

    public PythonHostConnectionManager(Activity mainActivity) {
        this.communicationHandler = new PythonHostCommunicationHandler();
        this.connection = new PythonHostServiceConnection( new PythonHostServiceConnection.ConnectionListener() {
            @Override
            public void onConnected(Messenger messenger) {
                communicationHandler.initializeCommunication(messenger);
            }
        });
        this.mainActivity = mainActivity;
    }

    public void connect() {
        Intent pythonHostLookup = new Intent("com.apython.python.pythonhost.PYTHON_APP_EXECUTE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            pythonHostLookup.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }

        try {
            this.mainActivity.startActivityForResult(pythonHostLookup, PYTHON_HOST_LOOKUP_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Python host is not installed.");
            // TODO: Handle this.
        }
    }

    public void closeConnection() {
        Log.d(TAG, "bound: " + this.connection.isBound());
        // Unbind from the service
        if (this.connection.isBound()) {
            this.mainActivity.unbindService(this.connection);
            //this.mBound = false;
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == PYTHON_HOST_LOOKUP_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Got result from python host: package_name = " + result.getStringExtra("package_name") + ", service_name = " + result.getStringExtra("service_name"));
                // Create intent to connect to the python host
                Intent intent = new Intent();
                intent.setClassName(result.getStringExtra("package_name"), result.getStringExtra("service_name"));
                // Bind to the service
                Log.d(TAG, intent + "");
                try {
                    this.mainActivity.bindService(intent, this.connection, Context.BIND_AUTO_CREATE);
                } catch (SecurityException se) {
                    Log.e(TAG, "Could not connect to the python host.");
                    se.printStackTrace();
                    // TODO: Handle this
                    this.mainActivity.finish();
                }
            }
            // TODO: Handle else.
        }
    }

}
