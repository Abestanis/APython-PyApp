package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/*
 * Handles the connection with the Python host.
 *
 * Created by Sebastian on 28.05.2015.
 */

public class PythonHostConnectionManager {

    // The request code used for the Python host
    public static final int PYTHON_HOST_REQUEST_CODE = 0;
    // The protocol version used by this app
    public static final int PROTOCOL_VERSION = 0;
    // The communication handler that receives all incoming messages from the Python host.
    PythonHostCommunicationHandler communicationHandler;

    public PythonHostConnectionManager() {
        this.communicationHandler = new PythonHostCommunicationHandler(new PythonHostCommunicationHandler.ConnectionManager() {
            @Override
            public void onMessage(Message message) {
                handleMessage(message);
            }
        });
    }

    public IBinder getMessengerBinder() {
        return this.communicationHandler.getBinder();
    }

    public void connectToPythonHost(Activity mainActivity) {
        Intent startIntent = new Intent("com.python.pythonhost.PYTHON_APP_EXECUTE");
        startIntent.putExtra("protocolVersion", PROTOCOL_VERSION);
        startIntent.putExtra("appTag", MainActivity.TAG);
        startIntent.putExtra("packageName", mainActivity.getPackageName());
        startIntent.putExtra("serviceName", PythonHostCommunicationService.class.getName());
        mainActivity.startActivityForResult(startIntent, PYTHON_HOST_REQUEST_CODE);
    }

    public void handleActivityResult(Activity mainActivity, int requestCode, int resultCode, Intent result) {
        if (requestCode == PYTHON_HOST_REQUEST_CODE) {
            Log.d(MainActivity.TAG, "Python App finished.");
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.w(MainActivity.TAG, "Python App finished with an error!");
                if (result != null) {
                    String errorMessage = result.getStringExtra("errorMessage");
                    if (errorMessage != null) {
                        Log.w(MainActivity.TAG, errorMessage);
                    }
                }
                // TODO: Handle
                mainActivity.finish();
            } else {
                // App executed normally
                mainActivity.finish();
            }
        }
    }

    public void handleMessage(Message message) {
        // TODO: Implement
    }
}
