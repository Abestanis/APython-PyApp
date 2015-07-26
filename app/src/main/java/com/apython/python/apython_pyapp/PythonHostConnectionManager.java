package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;

/*
 * Handles the connection with the Python host.
 *
 * Created by Sebastian on 28.05.2015.
 */

public class PythonHostConnectionManager {
    // The protocol version used by this app
    public static final int PROTOCOL_VERSION = 0;

    public void connectToPythonHost(Activity mainActivity) {
        Intent startIntent = new Intent("com.python.pythonhost.PYTHON_APP_GET_EXECUTION_INFO");
        startIntent.putExtra("protocolVersion", PROTOCOL_VERSION);
        startIntent.putExtra("package", this.getClass().getPackage().getName());
        startIntent.putExtra("launchClass", PythonExecuteActivity.class.getCanonicalName());
        startIntent.putExtra("requirements", "Twisted");
        try {
            mainActivity.startActivity(startIntent);
        } catch (ActivityNotFoundException e) {
            // TODO: Handle this better!
            Log.e(MainActivity.TAG, "Failed to start Python app: No Python interpreter found on system!");
        }
        mainActivity.finish();
    }
}
