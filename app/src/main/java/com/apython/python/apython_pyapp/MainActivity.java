package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;


public class MainActivity extends Activity {

    // The tag for this App // TODO: Maybe use the app name instead?
    public static final String TAG = "PythonApp";

    private static final int PYTHON_HOST_LOOKUP_REQUEST_CODE = 0;

    // A Connector for establishing the connection to the python host service.
    private PythonHostServiceConnector connector = new PythonHostServiceConnector(this);

    // Messenger for communicating with the service python host service.
    private Messenger mService = null;

    // True if we have called bind on the python host service.
    private boolean mBound = false;

    public void setPythonAppConnection(Messenger service) {
        this.mService = service;
        this.mBound = true;
    }

    public void disconnectPythonAppConnection() {
        this.mService = null;
        this.mBound = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent pythonHostLookup = new Intent("com.apython.python.pythonhost.PYTHON_APP_EXECUTE");

        try {
            startActivityForResult(pythonHostLookup, PYTHON_HOST_LOOKUP_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Python host is not installed.");
            // TODO: Handle this.
        }
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "bound: " + this.mBound);
        super.onStop();
        // Unbind from the service
        if (this.mBound) {
            unbindService(this.connector);
            this.mBound = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == PYTHON_HOST_LOOKUP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Create intent to connect to the python host
                Intent intent = new Intent();
                intent.setClassName(result.getStringExtra("package_name"), result.getStringExtra("service_class_name"));
                // Bind to the service
                bindService(intent, this.connector, Context.BIND_AUTO_CREATE);
            }
            // TODO: Handle else.
        }
    }
}
